package com.leafyjava.pannellumtourmaker.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
    @Value("${application.domain}")
    private String domain;

    @Value("${spring.application.path}")
    private String path;

    @GetMapping()
    public String index() {
        return String.format("redirect:%s%s/app", domain, path);
    }
}
