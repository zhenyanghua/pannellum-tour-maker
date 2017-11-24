package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.Scene;
import com.leafyjava.pannellumtourmaker.domains.Tour;

import java.util.List;

public interface TourService {
    void createTourFromFiles(String tour);

    List<Tour> findAllTours();

    Tour findOne(String name);
}
