package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.Task;

import java.util.List;

public interface TaskService {
    List<Task> findAll();
    Task insert(Task task);
    void delete(Task task);
    Task save(Task task);
    List<Task> findAllPaginatedAndSorted(int page, int size, String sortBy, String sortOrder);
}
