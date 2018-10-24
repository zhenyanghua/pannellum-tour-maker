package com.leafyjava.pannellumtourmaker.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@ControllerAdvice
public class HomeController {

    @Value("${application.domain}")
    private String domain;

    @Value("${spring.application.path}")
    private String path;

    private String baseUrl;

    public HomeController() {
        baseUrl = domain + path;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:" + baseUrl + "/tours";
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
