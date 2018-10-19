package com.leafyjava.pannellumtourmaker.repositories;

import com.leafyjava.pannellumtourmaker.domains.TourGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourGroupRepository extends MongoRepository<TourGroup, String> {
    boolean existsTourGroupByName(String name);
}