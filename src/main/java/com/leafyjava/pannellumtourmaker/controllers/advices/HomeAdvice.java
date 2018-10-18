package com.leafyjava.pannellumtourmaker.controllers.advices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class HomeAdvice {

    @Value("${spring.application.path}")
    private String serverPath;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("serverPath", serverPath);
    }
}
