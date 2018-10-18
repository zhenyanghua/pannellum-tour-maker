package com.leafyjava.pannellumtourmaker.repositories;

import com.leafyjava.pannellumtourmaker.domains.Tour;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends MongoRepository<Tour, String> {

}
