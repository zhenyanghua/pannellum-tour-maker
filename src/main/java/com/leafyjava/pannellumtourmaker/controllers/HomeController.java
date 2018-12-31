package com.leafyjava.pannellumtourmaker.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${application.domain}")
    private String domain;

    @Value("${spring.application.path}")
    private String path;

    @GetMapping("/")
    public String index() {
        String serverPath = domain + path + "/app";

        return String.format("redirect:%s", serverPath);
    }
}
