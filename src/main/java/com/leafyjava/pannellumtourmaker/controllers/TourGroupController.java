package com.leafyjava.pannellumtourmaker.controllers;

import com.leafyjava.pannellumtourmaker.domains.TourGroup;
import com.leafyjava.pannellumtourmaker.services.TourGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/groups")
public class TourGroupController {

    @Value("${application.baseUrl}")
    private String baseUrl;

    private TourGroupService tourGroupService;

    @Autowired
    public TourGroupController(final TourGroupService tourGroupService) {
        this.tourGroupService = tourGroupService;
    }

    @GetMapping()
    public String PagedTourGroupsView(@PageableDefault Pageable pageable,
                                     Model model) {
        Page<TourGroup> pagedGroups = tourGroupService.findAll(pageable);

        model.addAttribute("pagedGroups", pagedGroups);

        return "groups/list";
    }

    @GetMapping("{name}")
    public String oneTourGroupView(@PathVariable("name") String groupName,
                               Model model) {
        TourGroup group = tourGroupService.findOne(groupName);

        model.addAttribute("group", group);

        return "groups/item";
    }

    @GetMapping("create")
    public String createTourGroupView(Model model) {
        TourGroup group = new TourGroup();

        model.addAttribute("group", group);

        return "groups/create";
    }

    @GetMapping("{name}/edit")
    public String createTourGroupView(@PathVariable("name") String groupName,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (!tourGroupService.existsByGroupName(groupName)) {
            redirectAttributes.addFlashAttribute("error",
                String.format("Group name '%s' doesn't exist.", groupName));
            return "redirect:" + baseUrl + "/groups/" + groupName;
        }

        TourGroup group = tourGroupService.findOne(groupName);

        model.addAttribute("group", group);

        return "groups/edit";
    }

    @PostMapping()
    public String addTourGroup(@ModelAttribute TourGroup tourGroup,
                               RedirectAttributes redirectAttributes) {
        if (tourGroupService.existsByGroupName(tourGroup.getName())) {
            redirectAttributes.addFlashAttribute("error",
                String.format("Group name '%s' was taken.", tourGroup.getName()));
            return "redirect:" + baseUrl + "/groups/create";
        }
        TourGroup group = tourGroupService.insert(tourGroup);
        return "redirect:" + baseUrl + "/groups/" + group.getName();
    }

    @PutMapping("{name}")
    public String updateTourGroup(@PathVariable("name") String groupName,
                                  @ModelAttribute TourGroup tourGroup,
                                  RedirectAttributes redirectAttributes) {
        if (!groupName.equals(tourGroup.getName())) {
            redirectAttributes.addFlashAttribute("error",
                "Bad request - group name doesn't match");
            return "redirect:" + baseUrl + "/groups/" + groupName + "/edit";
        }

        if (!tourGroupService.existsByGroupName(groupName)) {
            redirectAttributes.addFlashAttribute("error",
                String.format("Group name '%s' doesn't exist.", groupName));
            return "redirect:" + baseUrl + "/groups/" + groupName + "/edit";
        }

        tourGroupService.update(tourGroup);

        return "redirect:" + baseUrl + "/groups/" + groupName;
    }

    @DeleteMapping("{name}")
    public String deleteTourGroup(@PathVariable("name") String groupName,
                                  RedirectAttributes redirectAttributes) {
        if (!tourGroupService.existsByGroupName(groupName)) {
            redirectAttributes.addFlashAttribute("error",
                String.format("Group name '%s' doesn't exist.", groupName));
            return "redirect:" + baseUrl + "/groups/" + groupName;
        }

        tourGroupService.delete(groupName);

        return "redirect:" + baseUrl + "/groups";
    }

}
