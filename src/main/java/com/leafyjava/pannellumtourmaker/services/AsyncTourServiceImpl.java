package com.leafyjava.pannellumtourmaker.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leafyjava.pannellumtourmaker.domains.TourMessage;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.leafyjava.pannellumtourmaker.utils.SupportedTourUploadType.*;

@Service
public class AsyncTourServiceImpl implements AsyncTourService {
    private RabbitTemplate rabbitTemplate;
    private Queue toursZipMultiresQueue;
    private Queue toursZipEquirectangularQueue;
    private FileUploadService fileUploadService;
    private TourService tourService;

    @Autowired
    public AsyncTourServiceImpl(final RabbitTemplate rabbitTemplate,
                                @Qualifier("tours.zip.multires") final Queue toursZipMultiresQueue,
                                @Qualifier("tours.zip.equirectangular") final Queue toursZipEquirectangularQueue,
                                final FileUploadService fileUploadService,
                                final TourService tourService) {
        this.rabbitTemplate = rabbitTemplate;
        this.toursZipMultiresQueue = toursZipMultiresQueue;
        this.toursZipEquirectangularQueue = toursZipEquirectangularQueue;
        this.fileUploadService = fileUploadService;
        this.tourService = tourService;
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

    @RabbitListener(queues = "tours.zip.multires")
    public void receiveInToursZipMultires(String message) {
        ObjectMapper mapper = new ObjectMapper();
        TourMessage tourMessage = null;
        try {
            tourMessage = mapper.readValue(message, TourMessage.class);
            fileUploadService.store(tourMessage.getName(), MULTIRES, tourMessage.getFile());
            tourService.createTourFromMultires(tourMessage.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "tours.zip.equirectangular")
    public void receiveInToursZipEquirectangular(String message) {
        ObjectMapper mapper = new ObjectMapper();
        TourMessage tourMessage = null;
        try {
            tourMessage = mapper.readValue(message, TourMessage.class);
            fileUploadService.store(tourMessage.getName(), EQUIRECTANGULAR, tourMessage.getFile());
            tourService.convertToMultiresFromEquirectangular(tourMessage.getName());
            tourService.createTourFromMultires(tourMessage.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
