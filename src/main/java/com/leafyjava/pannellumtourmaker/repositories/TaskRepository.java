package com.leafyjava.pannellumtourmaker.repositories;

import com.leafyjava.pannellumtourmaker.domains.Task;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, ObjectId> {
}
