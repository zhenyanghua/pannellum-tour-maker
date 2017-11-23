package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.Scene;
import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class TourServiceImpl implements TourService{

    private StorageProperties storageProperties;

    @Autowired
    public TourServiceImpl(final StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void createTourFromFiles(final String tour) {
        Path tourPath = Paths.get(storageProperties.getTourLocation()).resolve(tour);
        System.out.println(tourPath.toString());
    }

    @Override
    public List<Scene> getScenesByTour(final String tour) {
        return null;
    }
}
