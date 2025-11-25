package com.example.icslog.adapters.in.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.icslog.application.port.in.IngestSnmpEventPort;
import com.example.icslog.domain.model.SnmpEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class SnmpJsonFileIngestor {

    private final IngestSnmpEventPort ingestSnmpEventPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${icslog.ingest.snmp-json-path:}")
    private String snmpJsonPath;

    @PostConstruct
    public void ingestFromFile() {
        if (snmpJsonPath == null || snmpJsonPath.isBlank()) {
            return;
        }

        File file = new File(snmpJsonPath);
        if (!file.exists()) {
            System.err.println("SNMP JSON file not found: " + snmpJsonPath);
            return;
        }

        System.out.println("Reading SNMP JSON file (array support): " + snmpJsonPath);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ( (line = br.readLine()) != null ) {
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
            System.err.println("Failed to read SNMP JSON file: " + e.getMessage());
        }
    }

    private void processOneRecord(JsonNode item) {
        JsonNode source = item.path("_source");
        JsonNode layers = source.path("layers");

        JsonNode snmpNode = layers.path("snmp");
        if (snmpNode.isMissingNode() || snmpNode.isNull()) {
            return; 
        }

        Instant frameTime = extractFrameTime(layers.path("frame"));

        String srcIp = extractSrcIp(layers);
        String dstIp = extractDstIp(layers);

        String community = snmpNode.path("snmp.community").asText(null);
        String version   = snmpNode.path("snmp.version").asText(null);
        String dataType  = snmpNode.path("snmp.pdu_type").asText(null);

        VarBind vb = extractFirstVarBind(snmpNode);

        String oid   = vb.oid();
        String value = vb.value();

        String rawJson = item.toString();

        SnmpEvent event = SnmpEvent.create(
                frameTime,
                srcIp,
                dstIp,
                community,
                version,
                dataType,
                oid,
                value,
                rawJson
        );

        ingestSnmpEventPort.ingest(event);
    }

    private Instant extractFrameTime(JsonNode frameNode) {
        JsonNode utc = frameNode.path("frame.time_utc");
        if (utc.isTextual()) {
            try {
                return Instant.parse(utc.asText());
            } catch (DateTimeParseException ignored) {}
        }

        return Instant.now();
    }

    private String extractSrcIp(JsonNode layers) {
        JsonNode ip = layers.path("ip");
        if (!ip.isMissingNode()) {
            JsonNode src = ip.path("ip.src");
            if (src.isTextual()) {
                return src.asText();
            }
        }
        return null;
    }

    private String extractDstIp(JsonNode layers) {
        JsonNode ip = layers.path("ip");
        if (!ip.isMissingNode()) {
            JsonNode dst = ip.path("ip.dst");
            if (dst.isTextual()) {
                return dst.asText();
            }
        }
        return null;
    }

    private VarBind extractFirstVarBind(JsonNode snmpNode) {
        JsonNode dataTree = snmpNode.path("snmp.data_tree");
        if (dataTree.isMissingNode()) {
            return new VarBind(null, null);
        }

        JsonNode elem = dataTree.path("snmp.get_response_element");
        if (elem.isMissingNode()) {
            elem = dataTree.path("snmp.get_next_request_element");
        }
        if (elem.isMissingNode()) {
            elem = dataTree.path("snmp.get_request_element");
        }
        if (elem.isMissingNode()) {
            return new VarBind(null, null);
        }

        JsonNode vbTree = elem.path("snmp.variable_bindings_tree");
        if (vbTree.isMissingNode()) {
            return new VarBind(null, null);
        }

        var fieldNames = vbTree.fieldNames();
        if (!fieldNames.hasNext()) {
            return new VarBind(null, null);
        }

        String firstKey = fieldNames.next();
        JsonNode vb = vbTree.path(firstKey);

        String oid = vb.path("snmp.value_oid").asText(null);
        if (oid == null || oid.isBlank()) {
            oid = vb.path("snmp.oid").asText(null);
        }

        String value = null;
        JsonNode octets = vb.path("snmp.value.octets");
        if (octets.isTextual()) {
            value = hexToAscii(octets.asText());
        }

        if (value == null) {
            JsonNode plainVal = vb.path("snmp.value");
            if (plainVal.isTextual()) {
                value = plainVal.asText();
            }
        }

        if (value == null) {
            value = firstKey; 
        }

        return new VarBind(oid, value);
    }

    private String hexToAscii(String hexWithColons) {
        String[] parts = hexWithColons.split(":");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            try {
                int val = Integer.parseInt(p, 16);
                sb.append((char) val);
            } catch (NumberFormatException ignored) {}
        }
        return sb.toString();
    }

    private record VarBind(String oid, String value) {}
}
