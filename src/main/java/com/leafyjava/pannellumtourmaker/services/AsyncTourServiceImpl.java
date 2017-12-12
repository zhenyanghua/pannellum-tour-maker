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

import static com.leafyjava.pannellumtourmaker.utils.FileConstants.MULTIRES;
import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOURS_ADD_SCENE;
import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOURS_DELETE_SCENE;
import static com.leafyjava.pannellumtourmaker.utils.QueueNames.TOURS_NEW;

@Service
public class AsyncTourServiceImpl implements AsyncTourService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RabbitTemplate rabbitTemplate;
    private Queue toursQueueNew;
    private Queue toursQueueAddScene;
    private Queue toursQueueDeleteScene;
    private TourService tourService;
    private StorageService storageService;
    private StorageProperties storageProperties;
    private TaskService taskService;

    @Autowired
    public AsyncTourServiceImpl(final RabbitTemplate rabbitTemplate,
                                @Qualifier(TOURS_NEW) final Queue toursQueueNew,
                                @Qualifier(TOURS_ADD_SCENE) final Queue toursQueueAddScene,
                                @Qualifier(TOURS_DELETE_SCENE) final Queue toursQueueDeleteScene,
                                final TourService tourService,
                                final StorageService storageService,
                                final StorageProperties storageProperties,
                                final TaskService taskService) {
        this.rabbitTemplate = rabbitTemplate;
        this.toursQueueNew = toursQueueNew;
        this.toursQueueAddScene = toursQueueAddScene;
        this.toursQueueDeleteScene = toursQueueDeleteScene;
        this.tourService = tourService;
        this.storageService = storageService;
        this.storageProperties = storageProperties;
        this.taskService = taskService;
    }

    @Override
    public void sendToToursNew(final TourMessage tourMessage) {
        serializeAndSend(tourMessage, toursQueueNew);
    }

    @Override
    public void sendToToursAddScene(final TourMessage tourMessage) {
        serializeAndSend(tourMessage, toursQueueAddScene);
    }

    @Override
    public void sendToToursDeleteSceneFiles(final TourMessage tourMessage) {
        serializeAndSend(tourMessage, toursQueueDeleteScene);
    }

    @RabbitListener(queues = TOURS_NEW)
    public void receiveInToursNew(String message) {
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

    @RabbitListener(queues = TOURS_ADD_SCENE)
    public void receiveInToursAddScene(String message) {
        ObjectMapper mapper = new ObjectMapper();
        TourMessage tourMessage = null;
        try {
            tourMessage = mapper.readValue(message, TourMessage.class);
            handleRunningTask(tourMessage);

            String tourName = tourMessage.getName();
            String mapPath = null;

            storageService.storeTourContent(tourName, tourMessage.getTourFile());

            Map<String, PhotoMeta> photoMetaMap = tourService.convertToMultiresFromEquirectangular(tourName);

            tourService.addToTourFromMultires(tourName, photoMetaMap, tourMessage.getNorthOffset());

            handleSucceededTask(tourMessage);
        } catch (Exception e) {
            logger.error("Failed to process received equirectangular tour.", e);
            handleFailedTask(tourMessage);
        }
    }

    @RabbitListener(queues = TOURS_DELETE_SCENE)
    public void receiveInToursDeleteScene(String message) {
        ObjectMapper mapper = new ObjectMapper();
        TourMessage tourMessage = null;
        try {
            tourMessage = mapper.readValue(message, TourMessage.class);
            handleRunningTask(tourMessage);

            String tourName = tourMessage.getName();
            String sceneId = tourMessage.getDeletedSceneId();

            storageService.delete(Paths.get(storageProperties.getTourLocation())
                .resolve(tourName).resolve(MULTIRES).resolve(sceneId));

            handleSucceededTask(tourMessage);
        } catch (Exception e) {
            logger.error("Failed to delete tour tile images.", e);
            handleFailedTask(tourMessage);
        }
    }

    private void serializeAndSend(final TourMessage tourMessage, final Queue queue) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = mapper.writeValueAsString(tourMessage);
            rabbitTemplate.convertAndSend(queue.getName(), message);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize TourMessage", e);
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
