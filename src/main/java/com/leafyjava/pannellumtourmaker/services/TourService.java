package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.PhotoMeta;
import com.leafyjava.pannellumtourmaker.domains.Tour;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface TourService {
    void createTourFromMultires(String tourName, Map<String, PhotoMeta> metaMap, String mapPath, int northOffset);

    void addToTourFromMultires(String tourName, Map<String, PhotoMeta> metaMap, int northOffset);

    Map<String, PhotoMeta> convertToMultiresFromEquirectangular(String tourName);

    File createTempFileFromMultipartFile(MultipartFile file);

    String getMapPath(String tourName, File mapFile);

    List<Tour> findAllTours();

    List<String> findAllTourNames();

    Tour findOne(String name);

    Tour save(Tour tour);
}
