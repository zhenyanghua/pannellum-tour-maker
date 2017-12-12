package com.leafyjava.pannellumtourmaker.domains;

import com.leafyjava.pannellumtourmaker.utils.TaskOperation;
import com.leafyjava.pannellumtourmaker.utils.TaskStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Document(collection = "tasks")
public class Task implements Serializable {
    @Id
    private UUID id;
    private String name;
    private TaskOperation operation;
    private Date createdDateTime;
    private Date startDateTime;
    private Date endDateTime;
    private TaskStatus status;

    public Task() {
        this.id = UUID.randomUUID();
        this.createdDateTime = new Date();
        this.status = TaskStatus.QUEUED;
    }

    public Task(final String name, final TaskOperation operation) {
        this.name = name;
        this.operation = operation;
        this.id = UUID.randomUUID();
        this.createdDateTime = new Date();
        this.status = TaskStatus.QUEUED;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public TaskOperation getOperation() {
        return operation;
    }

    public void setOperation(final TaskOperation operation) {
        this.operation = operation;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(final Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(final Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(final TaskStatus status) {
        this.status = status;
    }
}
