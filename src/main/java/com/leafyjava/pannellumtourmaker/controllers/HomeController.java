package com.leafyjava.pannellumtourmaker.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app")
public class HomeController {

    @Value("${application.domain}")
    private String domain;

    @Value("${spring.application.path}")
    private String path;

    @GetMapping()
    public String index() {
        return String.format("redirect:%s%s/app/tours", domain, path);
    }

    @GetMapping("/tasks")
    public String tasks() {
        return "tasks";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload/new";
    }

    @GetMapping("/upload-exist")
    public String uploadExist() {
        return "upload/exist";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "errors/access-denied";
    }
}
