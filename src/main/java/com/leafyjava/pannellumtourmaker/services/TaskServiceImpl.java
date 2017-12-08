package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.Task;
import com.leafyjava.pannellumtourmaker.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl extends AbstractRawService<Task> implements TaskService {

    private TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(final TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task insert(Task task) {
        return taskRepository.insert(task);
    }

    @Override
    public void delete(final Task task) {
        taskRepository.delete(task);
    }

    @Override
    public Task save(final Task task) {
        return taskRepository.save(task);
    }

    @Override
    protected PagingAndSortingRepository<Task, String> getDao() {
        return taskRepository;
    }
}
