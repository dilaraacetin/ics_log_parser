package com.example.icslog.adapters.in.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.icslog.application.port.in.IngestModbusEventPort;
import com.example.icslog.domain.model.ModbusEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ModbusJsonFileIngestor {

    private final IngestModbusEventPort ingestPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${icslog.ingest.modbus-json-path:}")
    private String modbusJsonPath;

    @PostConstruct
    public void ingestFromFile() {
        if (modbusJsonPath == null || modbusJsonPath.isBlank()) {
            return;
        }

        File file = new File(modbusJsonPath);
        if (!file.exists()) {
            System.err.println("Modbus JSON file not found: " + modbusJsonPath);
            return;
        }

        System.out.println("Reading Modbus JSON file (array support): " + modbusJsonPath);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JsonNode root = objectMapper.readTree(sb.toString());

            if (root.isArray()) {
                for (JsonNode item : root) {
                    processOneRecord(item);
                }
            } else {
                processOneRecord(root);
            }

        } catch (Exception e) {
            System.err.println("Failed to read Modbus JSON file: " + e.getMessage());
        }
    }

    private void processOneRecord(JsonNode item) {
        JsonNode source = item.path("_source");
        JsonNode layers = source.path("layers");

        Instant frameTime = extractFrameTime(layers.path("frame"));

        String srcIp = extractIp(layers, "ip.src");
        String dstIp = extractIp(layers, "ip.dst");

        String hexPayload = null;

        JsonNode dataNode = layers.path("data").path("data.data");
        if (dataNode.isTextual()) {
            hexPayload = dataNode.asText();
        } else {
            JsonNode tcpPayload = layers.path("tcp").path("tcp.payload");
            if (tcpPayload.isTextual()) {
                hexPayload = tcpPayload.asText();
            }
        }

        if (hexPayload == null || hexPayload.isBlank()) {
            return;
        }

        ModbusFields fields = parseModbusPayload(hexPayload);
        if (fields == null) {
            return;
        }

        String rawJson = item.toString();

        ModbusEvent event = ModbusEvent.create(
                frameTime,
                srcIp,
                dstIp,
                fields.transactionId(),
                fields.protocolId(),
                fields.lengthField(),
                fields.unitId(),
                fields.functionCode(),
                fields.functionName(),
                fields.registerAddress(),   
                fields.registerValue(),     
                hexPayload,
                rawJson
        );


        ingestPort.ingest(event);
    }

    private Instant extractFrameTime(JsonNode frameNode) {
        JsonNode utc = frameNode.path("frame.time_utc");
        if (utc.isTextual()) {
            String text = utc.asText();
            try {
                return Instant.parse(text);
            } catch (DateTimeParseException e) {
                System.err.println("Could not parse frame.time_utc '" + text + "': " + e.getMessage());
            }
        }
        return null; 
    }

    private String extractIp(JsonNode layers, String fieldName) {
        JsonNode ipNode = layers.path("ip");
        if (!ipNode.isMissingNode()) {
            JsonNode val = ipNode.path(fieldName);
            if (val.isTextual()) {
                return val.asText();
            }
        }
        return null;
    }

    /**
     * Modbus/TCP MBAP header + PDU parse:
     * Byte sıralaması (TCP payload):
     * [0-1] Transaction ID
     * [2-3] Protocol ID
     * [4-5] Length
     * [6]   Unit ID
     * [7]   Function Code
     * [8..] PDU data (ör: register address, value vs.)
     */
    private ModbusFields parseModbusPayload(String hexWithColons) {
        String[] parts = hexWithColons.split(":");
        if (parts.length < 8) {
            System.err.println("Modbus payload too short for MBAP header: " + hexWithColons);
            return null;
        }

        byte[] bytes = new byte[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                bytes[i] = (byte) Integer.parseInt(parts[i], 16);
            } catch (NumberFormatException e) {
                System.err.println("Invalid hex byte in Modbus payload: " + parts[i]);
                return null;
            }
        }

        int transactionId = ((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF);
        int protocolId    = ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        int lengthField   = ((bytes[4] & 0xFF) << 8) | (bytes[5] & 0xFF);
        int unitId        =  (bytes[6] & 0xFF);
        int functionCode  =  (bytes[7] & 0xFF);

        String functionName = mapFunctionCode(functionCode);

        Integer registerAddress = null;
        Integer registerValue   = null;

        if (bytes.length >= 12) {
            registerAddress = ((bytes[8]  & 0xFF) << 8) | (bytes[9]  & 0xFF);
            registerValue   = ((bytes[10] & 0xFF) << 8) | (bytes[11] & 0xFF);
        }

        return new ModbusFields(
                transactionId,
                protocolId,
                lengthField,
                unitId,
                functionCode,
                functionName,
                registerAddress,
                registerValue
        );
    }


    private String mapFunctionCode(int fc) {
        return switch (fc) {
            case 1  -> "Read Coils";
            case 2  -> "Read Discrete Inputs";
            case 3  -> "Read Holding Registers";
            case 4  -> "Read Input Registers";
            case 5  -> "Write Single Coil";
            case 6  -> "Write Single Register";
            case 15 -> "Write Multiple Coils";
            case 16 -> "Write Multiple Registers";
            default -> "Unknown";
        };
    }

    private record ModbusFields(
            int transactionId,
            int protocolId,
            int lengthField,
            int unitId,
            int functionCode,
            String functionName,
            Integer registerAddress,   
            Integer registerValue      
    ) {}

}
