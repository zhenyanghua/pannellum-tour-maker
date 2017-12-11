package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.TourMessage;

public interface AsyncTourService {
    void sendToToursNew(TourMessage tourMessage);
    void sendToToursAdd(TourMessage tourMessage);
}
