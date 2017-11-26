package com.leafyjava.pannellumtourmaker.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leafyjava.pannellumtourmaker.controllers.FileUploadController;
import com.leafyjava.pannellumtourmaker.domains.Scene;
import com.leafyjava.pannellumtourmaker.domains.Tour;
import com.leafyjava.pannellumtourmaker.exceptions.TourAlreadyExistsException;
import com.leafyjava.pannellumtourmaker.exceptions.UnsupportedFileTreeException;
import com.leafyjava.pannellumtourmaker.repositories.TourRepository;
import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourServiceImpl implements TourService{

    private StorageProperties storageProperties;
    private TourRepository tourRepository;

    @Autowired
    public TourServiceImpl(final StorageProperties storageProperties,
                           final TourRepository tourRepository) {
        this.storageProperties = storageProperties;
        this.tourRepository = tourRepository;
    }

    @Override
    public void createTourFromFiles(final String tourName) {
        Path tourPath = Paths.get(storageProperties.getTourLocation()).resolve(tourName).resolve("multires");
        try {
            List<Scene> scenes = Files.walk(tourPath, 1)
                .filter(path -> !path.getFileName().toString().equalsIgnoreCase("multires") &&
                    !path.getFileName().toString().equalsIgnoreCase(".DS_Store"))
                .map(this::mapConfigToScene)
                .collect(Collectors.toList());

            Tour tour = new Tour();
            tour.setName(tourName);
            tour.setScenes(scenes);

            if(tourRepository.findOne(tourName) != null) {
                throw new TourAlreadyExistsException(tourName + " already exists in the tour collection.");
            } else {
                tourRepository.insert(tour);
            }

        } catch (IOException e) {
            throw new UnsupportedFileTreeException("Multi-resolution directory is not found.", e);
        }
    }

    @Override
    public List<Tour> findAllTours() {
        return tourRepository.findAll();
    }

    @Override
    public Tour findOne(final String name) {
        return tourRepository.findOne(name);
    }

    @Override
    public Tour save(final Tour tour) {
        return tourRepository.save(tour);
    }


    private Scene mapConfigToScene(Path scenePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Path config = scenePath.resolve("config.json");
            Scene scene = mapper.readValue(config.toFile(), Scene.class);
            String sceneId = scenePath.getFileName().toString();
            scene.setId(sceneId);
            scene.setTitle(sceneId);
            scene.setType("multires");

            UriComponents uriComponents = MvcUriComponentsBuilder.fromController(FileUploadController.class)
                .build();
            String base = uriComponents.toString().replace(uriComponents.getPath(), "");
            String basePath = base + "/" + scenePath.toString().replace(storageProperties.getTourLocation(), "tours");

            scene.getMultiRes().setBasePath(basePath);
            scene.setHotSpots(new ArrayList<>());
            return scene;
        } catch (IOException e) {
            throw new UnsupportedFileTreeException("Failed to read: " + scenePath, e);
        }
    }
}
