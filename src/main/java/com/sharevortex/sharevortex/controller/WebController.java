package com.sharevortex.sharevortex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
class WebController {

    @GetMapping("/receive/{token}")
    public String receiveFile(@PathVariable String token) {
        // Redirect to the API stream endpoint
        return "redirect:/api/stream/" + token;
    }
}
