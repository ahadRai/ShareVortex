package com.sharevortex.sharevortex.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private final Map<String, InputStream> sessions = new ConcurrentHashMap<>();

    public String createSession(MultipartFile file) throws IOException {
        String token = UUID.randomUUID().toString().substring(0, 10);
        PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(out);
        sessions.put(token, in);

        // Stream in a new thread
        new Thread(() -> {
            try {
                file.getInputStream().transferTo(out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        return token;
    }

    public InputStream getStream(String token) {
        return sessions.remove(token);
    }
}
