package com.sharevortex.sharevortex.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
    private final Map<String, SessionData> sessions = new ConcurrentHashMap<>();

    public static class SessionData {
        public final byte[] data;
        public final String filename;
        public final String contentType;
        
        public SessionData(byte[] data, String filename, String contentType) {
            this.data = data;
            this.filename = filename;
            this.contentType = contentType;
        }
    }

    public String createSession(MultipartFile file) throws IOException {
        String token = UUID.randomUUID().toString().substring(0, 10);
        
        // Read the entire file into memory for simplicity
        // In production, you might want to use temporary files or a database
        byte[] fileData = file.getBytes();
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        
        sessions.put(token, new SessionData(fileData, filename, contentType));
        
        logger.info("Created session with token: {} for file: {} ({} bytes)", 
                   token, filename, fileData.length);
        
        return token;
    }

    public InputStream getStream(String token) {
        SessionData sessionData = sessions.remove(token);
        if (sessionData == null) {
            logger.warn("Session not found for token: {}", token);
            return null;
        }
        
        logger.info("Retrieved session for token: {} ({} bytes)", token, sessionData.data.length);
        return new ByteArrayInputStream(sessionData.data);
    }
    
    public SessionData getSessionData(String token) {
        return sessions.get(token);
    }
    
    public void removeSession(String token) {
        sessions.remove(token);
        logger.info("Removed session for token: {}", token);
    }
    
    public boolean hasSession(String token) {
        return sessions.containsKey(token);
    }
    
    public void cleanupExpiredSessions() {
        // In a production environment, you might want to implement session expiration
        // For now, we'll keep it simple and let sessions remain until accessed
        logger.debug("Current active sessions: {}", sessions.size());
    }
}
