package com.leafyjava.pannellumtourmaker.repositories;

import com.leafyjava.pannellumtourmaker.domains.Tour;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TourRepository extends MongoRepository<Tour, String> {

}
