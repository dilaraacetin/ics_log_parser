package com.example.icslog.adapters.in.syslog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "icslog.syslog.udp", name = "enabled", havingValue = "true", matchIfMissing = false)
public class UdpSyslogServer implements Runnable {

    private final SyslogParser syslogParser;

    @Value("${icslog.syslog.port:1514}")
    private int port;

    @Value("${icslog.syslog.buffer-size:2048}")
    private int bufferSize;

    private DatagramSocket socket;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;

    @PostConstruct
    public void start() {
        try {
            socket = new DatagramSocket(port);
            executor.submit(this);
            System.out.println("Syslog UDP server started on port: " + port);
        } catch (SocketException e) {
            System.err.println("Could not start Syslog server on port " + port);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                byte[] buffer = new byte[bufferSize];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String rawMessage = new String(packet.getData(), 0, packet.getLength());
                String sourceIp = packet.getAddress().getHostAddress();
                syslogParser.parseAndIngest(rawMessage, sourceIp);
            } catch (IOException e) {
                if (!running && e.getMessage().contains("Socket closed")) return;
                System.err.println("Syslog socket error: " + e.getMessage());
            }
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) socket.close();
        executor.shutdown();
        System.out.println("Syslog UDP server stopped.");
    }
}
