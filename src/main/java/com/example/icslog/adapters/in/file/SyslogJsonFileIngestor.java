package com.example.icslog.adapters.in.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.icslog.application.port.in.IngestSyslogEventPort;
import com.example.icslog.domain.model.SyslogEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SyslogJsonFileIngestor {

    private final IngestSyslogEventPort ingestPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${icslog.ingest.syslog-json-path:}")
    private String syslogJsonPath;

    @PostConstruct
    public void ingestFromFile() {
        if (syslogJsonPath == null || syslogJsonPath.isBlank()) return;

        File file = new File(syslogJsonPath);
        if (!file.exists()) {
            System.err.println("Syslog JSON file not found: " + syslogJsonPath);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ( (line = br.readLine()) != null ) {
                line = line.trim();
                if (line.isEmpty()) continue;

                SyslogJsonRecord r = objectMapper.readValue(line, SyslogJsonRecord.class);

                SyslogEvent event = SyslogEvent.create(
                        r.MessageSourceAddress,
                        parseInstant(r.EventReceivedTime),
                        r.SourceModuleName,
                        r.SourceModuleType,
                        r.Hostname,
                        r.SyslogFacilityValue,
                        r.SyslogFacility,
                        r.SyslogSeverityValue,
                        r.SyslogSeverity,
                        r.SeverityValue,
                        r.Severity,
                        parseInstant(r.EventTime),
                        r.Message
                );
                ingestPort.ingest(event);
            }
        } catch (Exception e) {
            System.err.println("Failed to read syslog json: " + e.getMessage());
        }
    }

    private Instant parseInstant(String s) {
        if (s == null || s.isBlank()) return null;
        return Instant.parse(s.replace("+03:00","Z")); // çok kaba, istersen DateTimeFormatter kullan
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SyslogJsonRecord {
        public String MessageSourceAddress;
        public String EventReceivedTime;
        public String SourceModuleName;
        public String SourceModuleType;
        public String Hostname;
        public Integer SyslogFacilityValue;
        public String SyslogFacility;
        public Integer SyslogSeverityValue;
        public String SyslogSeverity;
        public Integer SeverityValue;
        public String Severity;
        public String EventTime;
        public String Message;
    }
}
