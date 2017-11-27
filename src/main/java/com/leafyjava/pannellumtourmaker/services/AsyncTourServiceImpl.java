package com.leafyjava.pannellumtourmaker.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leafyjava.pannellumtourmaker.domains.TourMessage;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = "tours")
public class AsyncTourServiceImpl implements AsyncTourService {
    private RabbitTemplate rabbitTemplate;
    private Queue queue;
    private FileUploadService fileUploadService;
    private TourService tourService;

    @Autowired
    public AsyncTourServiceImpl(final RabbitTemplate rabbitTemplate, final Queue queue, final FileUploadService fileUploadService, final TourService tourService) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
        this.fileUploadService = fileUploadService;
        this.tourService = tourService;
    }

    @Override
    public void send(TourMessage tourMessage) {
        String queueName = queue.getName();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = mapper.writeValueAsString(tourMessage);
            rabbitTemplate.convertAndSend(queueName, message);;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @RabbitHandler
    public void receive(String message) {
        ObjectMapper mapper = new ObjectMapper();
        TourMessage tourMessage = null;
        try {
            tourMessage = mapper.readValue(message, TourMessage.class);
            fileUploadService.store(tourMessage.getName(), tourMessage.getFile());
            tourService.createTourFromFiles(tourMessage.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
