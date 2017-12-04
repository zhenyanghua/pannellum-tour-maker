package com.leafyjava.pannellumtourmaker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/tours/{tour}")
    public String tourEdit() {
        return "tour-edit";
    }

    @GetMapping("/tours")
    public String tours() {
        return "tours";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
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
