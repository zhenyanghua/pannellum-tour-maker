package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.Scene;

import java.util.List;

public interface TourService {
    void createTourFromFiles(String tour);
    List<Scene> getScenesByTour(String tour);
}
