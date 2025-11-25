package com.example.icslog.adapters.in.syslog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.example.icslog.application.port.in.IngestEventPort;
import com.example.icslog.domain.model.Event;
import com.example.icslog.domain.model.Severity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SyslogParser {

    private final IngestEventPort ingestEventPort;

    private static final Pattern SYSLOG_PATTERN =
            Pattern.compile("^<(\\d+)>(?:[A-Za-z]{3}\\s+\\d{1,2}\\s[0-9:]{8})?\\s?(\\S+)?\\s?:?\\s?(.*)$");

    public void parseAndIngest(String rawMessage, String sourceIp) {
        System.out.println("Received from " + sourceIp + ": " + rawMessage.trim());

        Matcher matcher = SYSLOG_PATTERN.matcher(rawMessage.trim());
        if (!matcher.matches()) {
            System.err.println("Unparseable syslog: " + rawMessage.trim());
            return;
        }

        try {
            int priorityValue = Integer.parseInt(matcher.group(1));
            String messageContent = matcher.group(3);
            Severity severity = mapSeverity(priorityValue % 8);
            String deviceId = sourceIp;

            Event event = Event.create(deviceId, severity, messageContent, sourceIp);
            ingestEventPort.ingestEvent(event);
        } catch (Exception e) {
            System.err.println("Failed to parse syslog message: " + e.getMessage());
        }
    }

    private Severity mapSeverity(int syslogSeverity) {
        return switch (syslogSeverity) {
            case 0, 1, 2 -> Severity.CRITICAL;
            case 3 -> Severity.ERROR;
            case 4 -> Severity.WARNING;
            default -> Severity.INFO;
        };
    }
}
