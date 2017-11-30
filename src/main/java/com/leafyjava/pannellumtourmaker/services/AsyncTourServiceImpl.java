package com.leafyjava.pannellumtourmaker.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leafyjava.pannellumtourmaker.domains.Exif;
import com.leafyjava.pannellumtourmaker.domains.PhotoMeta;
import com.leafyjava.pannellumtourmaker.domains.TourMessage;
import com.leafyjava.pannellumtourmaker.storage.services.StorageService;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOUR_ZIP_EQUIRECTANGULAR;
import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOUR_ZIP_MULTIRES;
import static com.leafyjava.pannellumtourmaker.utils.SupportedTourUploadType.EQUIRECTANGULAR;
import static com.leafyjava.pannellumtourmaker.utils.SupportedTourUploadType.MULTIRES;

@Service
public class AsyncTourServiceImpl implements AsyncTourService {
    private RabbitTemplate rabbitTemplate;
    private Queue toursZipMultiresQueue;
    private Queue toursZipEquirectangularQueue;
    private TourService tourService;
    private StorageService storageService;

    @Autowired
    public AsyncTourServiceImpl(final RabbitTemplate rabbitTemplate,
                                @Qualifier(TOUR_ZIP_MULTIRES) final Queue toursZipMultiresQueue,
                                @Qualifier(TOUR_ZIP_EQUIRECTANGULAR) final Queue toursZipEquirectangularQueue,
                                final TourService tourService,
                                final StorageService storageService) {
        this.rabbitTemplate = rabbitTemplate;
        this.toursZipMultiresQueue = toursZipMultiresQueue;
        this.toursZipEquirectangularQueue = toursZipEquirectangularQueue;
        this.tourService = tourService;
        this.storageService = storageService;
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
            storageService.storeZipContent(tourMessage.getName(), MULTIRES, tourMessage.getFile());
            tourService.createTourFromMultires(tourMessage.getName(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = TOUR_ZIP_EQUIRECTANGULAR)
    public void receiveInToursZipEquirectangular(String message) {
        ObjectMapper mapper = new ObjectMapper();
        TourMessage tourMessage;
        try {
            tourMessage = mapper.readValue(message, TourMessage.class);
            storageService.storeZipContent(tourMessage.getName(), EQUIRECTANGULAR, tourMessage.getFile());
            Map<String, PhotoMeta> metaMap = tourService.convertToMultiresFromEquirectangular(tourMessage.getName());
            tourService.createTourFromMultires(tourMessage.getName(), metaMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
