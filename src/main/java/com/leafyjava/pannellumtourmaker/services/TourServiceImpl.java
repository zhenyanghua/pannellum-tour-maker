package com.leafyjava.pannellumtourmaker.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.leafyjava.pannellumtourmaker.domains.Exif;
import com.leafyjava.pannellumtourmaker.domains.GPano;
import com.leafyjava.pannellumtourmaker.domains.PhotoMeta;
import com.leafyjava.pannellumtourmaker.domains.Scene;
import com.leafyjava.pannellumtourmaker.domains.Tour;
import com.leafyjava.pannellumtourmaker.exceptions.TourAlreadyExistsException;
import com.leafyjava.pannellumtourmaker.exceptions.UnsupportedFileTreeException;
import com.leafyjava.pannellumtourmaker.repositories.TourRepository;
import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import com.leafyjava.pannellumtourmaker.storage.services.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    @Value("${application.photo-preview.width}")
    private int previewWidth;

    @Value("${application.photo-preview.height}")
    private int previewHeight;

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
    public void createTourFromMultires(final String tourName,  Map<String, PhotoMeta> metaMap, String mapPath, int northOffset) {
        Path tourPath = Paths.get(storageProperties.getTourLocation()).resolve(tourName).resolve(MULTIRES);
        try {
            List<Scene> scenes = Files.walk(tourPath, 1)
                .filter(path -> !path.getFileName().toString().equalsIgnoreCase(MULTIRES) &&
                    !path.getFileName().toString().equalsIgnoreCase(".DS_Store"))
                .map(scenePath -> mapConfigToScene(scenePath, metaMap, northOffset))
                .collect(Collectors.toList());

            Tour tour = new Tour();
            tour.setName(tourName);
            tour.setScenes(scenes);
            tour.setMapPath(mapPath);
            if (scenes.size() > 0) {
                tour.setFirstScene(scenes.get(0).getId());
            }

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
    public Map<String, PhotoMeta> convertToMultiresFromEquirectangular(final String tourName) {
        Path equirectangularPath = Paths.get(storageProperties.getEquirectangularLocation()).resolve(tourName);
        Map<String, PhotoMeta> metaMap = new HashMap<>();

        try {
            Files.walk(equirectangularPath, 2)
                .filter(path -> path.toString().toLowerCase().endsWith(".jpg"))
                .forEach(path -> {
                    extractMeta(metaMap, path.toFile());
                    makeTiles(tourName, path);
                    makePreview(tourName, path);
                });
            FileSystemUtils.deleteRecursively(equirectangularPath.toFile());
        } catch (IOException e) {
            throw new UnsupportedFileTreeException("Failed to read equirectangular directory.", e);
        }

        return metaMap;
    }


    @Override
    public File createTempFileFromMultipartFile(final MultipartFile file) {
        return storageService.createTempFileFromMultipartFile(file);
    }

    @Override
    public String getMapPath(String tourName, File mapFile) {
        if (mapFile == null) return null;

        return baseUrl + "/" + TOURS + "/" + tourName + "/" + "map." + FilenameUtils.getExtension(mapFile.getName());
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

    private void makeTiles(final String tourName, final Path path) {
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
    }

    private void makePreview(final String tourName, final Path path) {
        try {
            BufferedImage originalImage = ImageIO.read(path.toFile());
            int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            BufferedImage resizeImagePng = resizeImage(originalImage, type);

            Path output = Paths.get(storageProperties.getTourLocation())
                .resolve(tourName).resolve(MULTIRES).resolve(FilenameUtils.getBaseName(path.toString())).resolve("preview.png");
            ImageIO.write(resizeImagePng, "png", output.toFile());
        } catch (IOException e) {
            logger.error("Failed to create preview image for " + path);
        }
    }

    private Scene mapConfigToScene(Path scenePath, Map<String, PhotoMeta> metaMap, int northOffset) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Path config = scenePath.resolve("config.json");
            Scene scene = mapper.readValue(config.toFile(), Scene.class);
            String sceneId = scenePath.getFileName().toString();
            scene.setId(sceneId);
            scene.setTitle(sceneId);
            scene.setType("multires");
            scene.setNorthOffset(northOffset);

            if (metaMap != null) {
                scene.setPhotoMeta(metaMap.get(sceneId));
            }

            String basePath = baseUrl + "/" + scenePath.toString().replace(storageProperties.getTourLocation(), TOURS);

            scene.getMultiRes().setBasePath(basePath);
            scene.setHotSpots(new ArrayList<>());
            return scene;
        } catch (IOException e) {
            throw new UnsupportedFileTreeException("Failed to read: " + scenePath, e);
        }
    }

    private void extractMeta(final Map<String, PhotoMeta> map, final File file) {
        try {
            PhotoMeta photoMeta = new PhotoMeta();

            photoMeta.setExif(extractExif(file));
            photoMeta.setGPano(extractGPano(file));

            map.put(FilenameUtils.getBaseName(file.getName()), photoMeta);


        } catch (ImageReadException | IOException e) {
            logger.warn("Failed to read meta data from image: " + file.getName());
        }
    }

    private GPano extractGPano(final File file) throws ImageReadException, IOException {
        GPano gPano = null;
        String xmpXml = Sanselan.getXmpXml(file);
        if (xmpXml != null && !xmpXml.isEmpty()) {
            XmlMapper mapper = new XmlMapper();
            String cleanedXml = extractText(xmpXml);
            gPano = mapper.readValue(cleanedXml, GPano.class);
        }
        return gPano;
    }

    private Exif extractExif(final File file) throws ImageReadException, IOException {
        Exif result = null;
        JpegImageMetadata metadata = (JpegImageMetadata) Sanselan.getMetadata(file);
        TiffImageMetadata exif = metadata.getExif();
        if (exif != null) {
            TiffImageMetadata.GPSInfo gps = exif.getGPS();
            if (gps != null) {
                final double longitude = gps.getLongitudeAsDegreesEast();
                final double latitude = gps.getLatitudeAsDegreesNorth();

                result = new Exif(longitude, latitude);
            }
        }
        return result;
    }

    private static String extractText(String content) {
        Pattern p = Pattern.compile("(<GPano:.*>)",
            Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            sb.append(m.group(1).replace("GPano:", ""));
        }
        return "<GPano>" + sb.toString() + "</GPano>";
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int type){
        BufferedImage resizedImage = new BufferedImage(previewWidth, previewHeight, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, previewWidth, previewHeight, null);
        g.dispose();

        return resizedImage;
    }
}
