package com.sharevortex.sharevortex.controller;


import com.sharevortex.sharevortex.model.FileSession;
import com.sharevortex.sharevortex.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/api")
public class HomeController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/send")
    public ResponseEntity<?> sendFile(@RequestParam("file") MultipartFile file) throws IOException {
        String token = sessionService.createSession(file);
        return ResponseEntity.ok().body(new FileSession(token));
    }

    @GetMapping("/stream/{token}")
    public ResponseEntity<InputStreamResource> streamFile(@PathVariable String token) throws IOException {
        InputStream inputStream = sessionService.getStream(token);
        if (inputStream == null) return ResponseEntity.notFound().build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=sharedfile");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }
}

@Controller
class WebController {
    
    @GetMapping("/receive/{token}")
    public String receiveFile(@PathVariable String token) {
        // Redirect to the API stream endpoint
        return "redirect:/api/stream/" + token;
    }
}

