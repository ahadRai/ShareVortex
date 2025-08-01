package com.sharevortex.sharevortex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
