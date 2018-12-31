package com.leafyjava.pannellumtourmaker.controllers.advices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
@RequestMapping("/app")
public class AppAdvice {
    public static final String SERVER_PATH = "serverPath";

    @Value("${application.domain}")
    private String domain;

    @Value("${spring.application.path}")
    private String path;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute(SERVER_PATH, domain + path + "/app");
    }
}
