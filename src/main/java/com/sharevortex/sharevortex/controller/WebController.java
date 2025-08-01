package com.sharevortex.sharevortex.controller;

import com.sharevortex.sharevortex.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private SessionService sessionService;

    @GetMapping("/receive/{token}")
    public String receiveFile(@PathVariable String token, Model model) {
        logger.info("Receive request for token: {}", token);
        
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Invalid token received: {}", token);
            model.addAttribute("error", "Invalid token provided");
            return "error";
        }
        
        if (!sessionService.hasSession(token)) {
            logger.warn("Token not found: {}", token);
            model.addAttribute("error", "File not found or token has expired");
            return "error";
        }
        
        // Redirect to the API stream endpoint
        logger.info("Redirecting to stream for token: {}", token);
        return "redirect:/api/stream/" + token;
    }
}
