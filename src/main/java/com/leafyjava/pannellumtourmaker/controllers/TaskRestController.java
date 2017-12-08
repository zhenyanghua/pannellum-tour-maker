package com.leafyjava.pannellumtourmaker.controllers;

import com.leafyjava.pannellumtourmaker.domains.Task;
import com.leafyjava.pannellumtourmaker.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.leafyjava.pannellumtourmaker.utils.QueryConstants.PAGE;
import static com.leafyjava.pannellumtourmaker.utils.QueryConstants.SIZE;
import static com.leafyjava.pannellumtourmaker.utils.QueryConstants.SORT_BY;
import static com.leafyjava.pannellumtourmaker.utils.QueryConstants.SORT_ORDER;

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

    @GetMapping(params = {PAGE, SIZE, SORT_BY, SORT_ORDER})
    public List<Task> findAllPaginatedAndSorted(
        @RequestParam(value = PAGE) final int page,
        @RequestParam(value = SIZE) final int size,
        @RequestParam(value = SORT_BY) final String sortBy,
        @RequestParam(value = SORT_ORDER) final String sortOrder) {
        return taskService.findAllPaginatedAndSorted(page, size, sortBy, sortOrder);
    }
}
