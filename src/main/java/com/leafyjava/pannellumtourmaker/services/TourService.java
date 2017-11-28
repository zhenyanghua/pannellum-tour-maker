package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.Tour;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface TourService {
    void createTourFromMultires(String tourName);

    void convertToMultiresFromEquirectangular(String tourName);

    File convertToFile(MultipartFile file);

    List<Tour> findAllTours();

    Tour findOne(String name);

    Tour save(Tour tour);
}
