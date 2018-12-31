package com.leafyjava.pannellumtourmaker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.leafyjava.pannellumtourmaker.controllers.advices.AppAdvice.SERVER_PATH;

@Controller
@RequestMapping("/app")
public class AppController {

    @GetMapping()
    public String index(Model model) {
        String serverPath = model.asMap().get(SERVER_PATH).toString();

        return String.format("redirect:%s/tours", serverPath);
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
