package com.leafyjava.pannellumtourmaker.controllers;

import com.leafyjava.pannellumtourmaker.services.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    private TourService tourService;

    @Autowired
    public HomeController(final TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping("/tours/{tour}")
    public String tourEdit(@PathVariable(value = "tour") String tour) {
        if (tourService.findOne(tour) == null) {
            return "redirect:/tours";
        }

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
