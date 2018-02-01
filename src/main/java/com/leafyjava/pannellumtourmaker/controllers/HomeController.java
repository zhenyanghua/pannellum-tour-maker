package com.leafyjava.pannellumtourmaker.controllers;

import com.leafyjava.pannellumtourmaker.services.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@ControllerAdvice
public class HomeController {

    @Value("${spring.application.path}")
    private String serverPath;

    private TourService tourService;

    @Autowired
    public HomeController(final TourService tourService) {
        this.tourService = tourService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("serverPath", serverPath);
    }

    @GetMapping("/")
    public String index() {
        return "redirect:" + serverPath + "/tours";
    }

    @GetMapping("/tours")
    public String tours() {
        return "tours";
    }

    @GetMapping("/tours/{tour}")
    public String tourEdit(@PathVariable(value = "tour") String tour) {
        if (tourService.findOne(tour) == null) {
            return "redirect:/tours";
        }

        return "tour-edit";
    }

    @GetMapping("/tasks")
    public String tasks() {
        return "tasks";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @GetMapping("/upload-exist")
    public String uploadExist() {
        return "upload-exist";
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
