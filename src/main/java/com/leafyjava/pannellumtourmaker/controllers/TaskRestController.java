package com.leafyjava.pannellumtourmaker.controllers;

import com.leafyjava.pannellumtourmaker.domains.Task;
import com.leafyjava.pannellumtourmaker.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/guest/tasks")
public class TaskRestController {

    private TaskService taskService;

    @Autowired
    public TaskRestController(final TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping()
    public List<Task> getTasks() {
        return taskService.findAll();
    }
}
