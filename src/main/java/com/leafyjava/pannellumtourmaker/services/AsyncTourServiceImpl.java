package com.leafyjava.pannellumtourmaker.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leafyjava.pannellumtourmaker.domains.PhotoMeta;
import com.leafyjava.pannellumtourmaker.domains.Task;
import com.leafyjava.pannellumtourmaker.domains.TourMessage;
import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import com.leafyjava.pannellumtourmaker.storage.services.StorageService;
import com.leafyjava.pannellumtourmaker.utils.TaskStatus;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOUR_ZIP_EQUIRECTANGULAR;

@Service
public class AsyncTourServiceImpl implements AsyncTourService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RabbitTemplate rabbitTemplate;
    private Queue toursZipEquirectangularQueue;
    private TourService tourService;
    private StorageService storageService;
    private StorageProperties storageProperties;
    private TaskService taskService;

    @Autowired
    public AsyncTourServiceImpl(final RabbitTemplate rabbitTemplate,
                                @Qualifier(TOUR_ZIP_EQUIRECTANGULAR) final Queue toursZipEquirectangularQueue,
                                final TourService tourService,
                                final StorageService storageService,
                                final StorageProperties storageProperties,
                                final TaskService taskService) {
        this.rabbitTemplate = rabbitTemplate;
        this.toursZipEquirectangularQueue = toursZipEquirectangularQueue;
        this.tourService = tourService;
        this.storageService = storageService;
        this.storageProperties = storageProperties;
        this.taskService = taskService;
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

    @RabbitListener(queues = TOUR_ZIP_EQUIRECTANGULAR)
    public void receiveInToursZipEquirectangular(String message) {
        ObjectMapper mapper = new ObjectMapper();
        TourMessage tourMessage = null;
        try {
            tourMessage = mapper.readValue(message, TourMessage.class);
            handleRunningTask(tourMessage);

            String tourName = tourMessage.getName();
            String mapPath = null;

            storageService.storeTourContent(tourName, tourMessage.getTourFile());

            if (tourMessage.getMapFile() != null) {
                String extension = "." + FilenameUtils.getExtension(tourMessage.getMapFile().getAbsolutePath());

                storageService.store(tourName,
                    tourMessage.getMapFile().toPath(),
                    Paths.get(storageProperties.getTourLocation()).resolve(tourName).resolve("map" + extension));
                mapPath = tourService.getMapPath(tourName, tourMessage.getMapFile());
            }

            Map<String, PhotoMeta> photoMetaMap = tourService.convertToMultiresFromEquirectangular(tourName);

            tourService.createTourFromMultires(tourName, photoMetaMap, mapPath, tourMessage.getNorthOffset());

            handleSucceededTask(tourMessage);
        } catch (Exception e) {
            logger.error("Failed to process received equirectangular tour.", e);
            handleFailedTask(tourMessage);
        }
    }

    private void handleRunningTask(final TourMessage tourMessage) {
        Task task = tourMessage.getTask();
        task.setStartDateTime(new Date());
        task.setStatus(TaskStatus.RUNNING);
        taskService.save(task);
    }

    private void handleSucceededTask(final TourMessage tourMessage) {
        Task task = tourMessage.getTask();
        task.setEndDateTime(new Date());
        task.setStatus(TaskStatus.SUCCEEDED);
        taskService.save(task);
    }

    private void handleFailedTask(final TourMessage tourMessage) {
        if (tourMessage != null) {
            Task task = tourMessage.getTask();
            task.setEndDateTime(new Date());
            task.setStatus(TaskStatus.FAILED);
            taskService.save(task);
        }
    }
}
