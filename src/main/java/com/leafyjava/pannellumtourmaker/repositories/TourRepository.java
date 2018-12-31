package com.leafyjava.pannellumtourmaker.repositories;

import com.leafyjava.pannellumtourmaker.domains.Tour;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TourRepository extends MongoRepository<Tour, String> {
    @Query(value = "{}", fields = "{ 'name': 1, 'alias': 1, 'groupName': 1, 'mapPath': 1 }")
    List<Tour> findAllWithBasic();

    @Query(value = "{ 'groupName': ?0 }", fields = "{ 'name': 1, 'alias': 1, 'groupName': 1, 'mapPath': 1 }")
    List<Tour> findByGroupNameWithBasic(String groupName);
}
