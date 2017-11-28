package com.leafyjava.pannellumtourmaker.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leafyjava.pannellumtourmaker.domains.Scene;
import com.leafyjava.pannellumtourmaker.domains.Tour;
import com.leafyjava.pannellumtourmaker.exceptions.TourAlreadyExistsException;
import com.leafyjava.pannellumtourmaker.exceptions.UnsupportedFileTreeException;
import com.leafyjava.pannellumtourmaker.repositories.TourRepository;
import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import com.leafyjava.pannellumtourmaker.storage.services.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.leafyjava.pannellumtourmaker.configs.WebConfig.TOURS;

@Service
public class TourServiceImpl implements TourService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String MULTIRES = "multires";

    @Value("${application.baseUrl}")
    private String baseUrl;

    @Value("${application.nona}")
    private String nona;

    private StorageService storageService;
    private StorageProperties storageProperties;
    private TourRepository tourRepository;

    @Autowired
    public TourServiceImpl(final StorageService storageService,
                           final StorageProperties storageProperties,
                           final TourRepository tourRepository) {
        this.storageService = storageService;
        this.storageProperties = storageProperties;
        this.tourRepository = tourRepository;
    }

    @Override
    public void createTourFromMultires(final String tourName) {
        Path tourPath = Paths.get(storageProperties.getTourLocation()).resolve(tourName).resolve(MULTIRES);
        try {
            List<Scene> scenes = Files.walk(tourPath, 1)
                .filter(path -> !path.getFileName().toString().equalsIgnoreCase(MULTIRES) &&
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
    public void convertToMultiresFromEquirectangular(final String tourName) {
        Path equirectangularPath = Paths.get(storageProperties.getEquirectangularLocation()).resolve(tourName);

        try {
            Files.walk(equirectangularPath, 2)
                .filter(path -> path.toString().toLowerCase().endsWith(".jpg"))
                .forEach(path -> {
                    String pyPath = null;
                    try {
                        pyPath = new ClassPathResource("generate.py").getURL().toString().substring(5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String output = storageProperties.getTourLocation() + "/" + tourName + "/" + MULTIRES + "/" +
                        FilenameUtils.getBaseName(path.toString());
                    String[] cmd = {
                        "/bin/bash",
                        "-c",
                        "python " + pyPath  + " -o " + output +
                            " -n " + nona + " " + path
                    };
                    try {
                        Process process = Runtime.getRuntime().exec(cmd);
                        int result = process.waitFor();
                        if (result != 0) {
                            logger.error("Command failed: " + String.join(" ", cmd));
                        }
                    } catch (IOException | InterruptedException e) {
                        logger.error("Command failed: " + String.join(" ", cmd), e);
                    }
                });
            FileSystemUtils.deleteRecursively(equirectangularPath.toFile());
        } catch (IOException e) {
            throw new UnsupportedFileTreeException("Failed to read equirectangular directory.", e);
        }
    }

    @Override
    public File convertToFile(final MultipartFile file) {
        return storageService.convertToFile(file);
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

            String basePath = baseUrl + "/" + scenePath.toString().replace(storageProperties.getTourLocation(), TOURS);

            scene.getMultiRes().setBasePath(basePath);
            scene.setHotSpots(new ArrayList<>());
            return scene;
        } catch (IOException e) {
            throw new UnsupportedFileTreeException("Failed to read: " + scenePath, e);
        }
    }
}
