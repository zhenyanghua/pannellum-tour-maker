package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.TourMessage;

public interface AsyncTourService {
    void sendToToursNew(TourMessage tourMessage);
    void sendToToursAddScene(TourMessage tourMessage);
    void sendToToursDeleteSceneFiles(TourMessage tourMessage);
    void sendToToursDeleteFiles(TourMessage tourMessage);
}
