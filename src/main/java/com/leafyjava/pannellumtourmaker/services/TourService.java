package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.Tour;

import java.util.List;

public interface TourService {
    void createTourFromMultires(String tourName);
    void convertToMultiresFromEquirectangular(String tourName);

    List<Tour> findAllTours();

    Tour findOne(String name);

    Tour save(Tour tour);
}
