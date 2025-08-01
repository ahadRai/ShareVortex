package com.sharevortex.sharevortex.controller;


import com.sharevortex.sharevortex.model.FileSession;
import com.sharevortex.sharevortex.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@RestController
@RequestMapping("/api")
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private SessionService sessionService;

    @PostMapping("/send")
    public ResponseEntity<?> sendFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }
            
            logger.info("Received file upload request: {} ({} bytes)", file.getOriginalFilename(), file.getSize());
            
            String token = sessionService.createSession(file);
            logger.info("Created session with token: {}", token);
            
            return ResponseEntity.ok().body(new FileSession(token));
        } catch (IOException e) {
            logger.error("Error processing file upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during file upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred");
        }
    }

    @GetMapping("/stream/{token}")
    public ResponseEntity<?> streamFile(@PathVariable String token) {
        try {
            logger.info("Stream request for token: {}", token);
            
            InputStream inputStream = sessionService.getStream(token);
            if (inputStream == null) {
                logger.warn("Token not found or expired: {}", token);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("File not found or token expired");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=sharedfile");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            
            logger.info("Streaming file for token: {}", token);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            logger.error("Error streaming file for token: {}", token, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error streaming file: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/stream/{token}", method = RequestMethod.HEAD)
    public ResponseEntity<?> checkToken(@PathVariable String token) {
        logger.info("Token check request for: {}", token);
        
        if (sessionService.hasSession(token)) {
            logger.info("Token exists: {}", token);
            return ResponseEntity.ok().build();
        } else {
            logger.warn("Token not found: {}", token);
            return ResponseEntity.notFound().build();
        }
    }
}

