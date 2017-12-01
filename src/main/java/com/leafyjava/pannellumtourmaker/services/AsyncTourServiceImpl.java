package com.leafyjava.pannellumtourmaker.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leafyjava.pannellumtourmaker.domains.PhotoMeta;
import com.leafyjava.pannellumtourmaker.domains.TourMessage;
import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import com.leafyjava.pannellumtourmaker.storage.services.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOUR_ZIP_EQUIRECTANGULAR;
import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOUR_ZIP_MULTIRES;
import static com.leafyjava.pannellumtourmaker.utils.SupportedTourUploadType.EQUIRECTANGULAR;
import static com.leafyjava.pannellumtourmaker.utils.SupportedTourUploadType.MULTIRES;

@Service
public class AsyncTourServiceImpl implements AsyncTourService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RabbitTemplate rabbitTemplate;
    private Queue toursZipMultiresQueue;
    private Queue toursZipEquirectangularQueue;
    private TourService tourService;
    private StorageService storageService;
    private StorageProperties storageProperties;

    @Autowired
    public AsyncTourServiceImpl(final RabbitTemplate rabbitTemplate,
                                @Qualifier(TOUR_ZIP_MULTIRES) final Queue toursZipMultiresQueue,
                                @Qualifier(TOUR_ZIP_EQUIRECTANGULAR) final Queue toursZipEquirectangularQueue,
                                final TourService tourService,
                                final StorageService storageService,
                                final StorageProperties storageProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.toursZipMultiresQueue = toursZipMultiresQueue;
        this.toursZipEquirectangularQueue = toursZipEquirectangularQueue;
        this.tourService = tourService;
        this.storageService = storageService;
        this.storageProperties = storageProperties;
    }

    @Override
    public void sendToToursZipMultires(TourMessage tourMessage) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = mapper.writeValueAsString(tourMessage);
            rabbitTemplate.convertAndSend(toursZipMultiresQueue.getName(), message);;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendToToursZipEquirectangular(final TourMessage tourMessage) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = mapper.writeValueAsString(tourMessage);
            rabbitTemplate.convertAndSend(toursZipEquirectangularQueue.getName(), message);;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = TOUR_ZIP_MULTIRES)
    public void receiveInToursZipMultires(String message) {
        ObjectMapper mapper = new ObjectMapper();
        TourMessage tourMessage;
        try {
            tourMessage = mapper.readValue(message, TourMessage.class);
            String tourName = tourMessage.getName();
            String mapPath = null;

            storageService.storeTourContent(tourName, MULTIRES, tourMessage.getTourFile());

            if (tourMessage.getMapFile() != null) {
                String extension = "." + FilenameUtils.getExtension(tourMessage.getMapFile().getAbsolutePath());

                storageService.store(tourName,
                    tourMessage.getMapFile().toPath(),
                    Paths.get(storageProperties.getTourLocation()).resolve(tourName).resolve("map" + extension));

                mapPath = tourService.getMapPath(tourName, tourMessage.getMapFile());
            }

            tourService.createTourFromMultires(tourName, null, mapPath);
        } catch (IOException e) {
            logger.error("Failed to process received multires tour.", e);
        }
    }

    @RabbitListener(queues = TOUR_ZIP_EQUIRECTANGULAR)
    public void receiveInToursZipEquirectangular(String message) {
        ObjectMapper mapper = new ObjectMapper();
        TourMessage tourMessage;
        try {
            tourMessage = mapper.readValue(message, TourMessage.class);
            String tourName = tourMessage.getName();
            String mapPath = null;

            storageService.storeTourContent(tourName, EQUIRECTANGULAR, tourMessage.getTourFile());

            if (tourMessage.getMapFile() != null) {
                String extension = "." + FilenameUtils.getExtension(tourMessage.getMapFile().getAbsolutePath());

                storageService.store(tourName,
                    tourMessage.getMapFile().toPath(),
                    Paths.get(storageProperties.getTourLocation()).resolve(tourName).resolve("map" + extension));
                mapPath = tourService.getMapPath(tourName, tourMessage.getMapFile());
            }

            Map<String, PhotoMeta> photoMetaMap = tourService.convertToMultiresFromEquirectangular(tourName);

            tourService.createTourFromMultires(tourName, photoMetaMap, mapPath);
        } catch (IOException e) {
            logger.error("Failed to process received equirectangular tour.", e);
        }
    }
}
