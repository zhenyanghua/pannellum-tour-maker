package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.TourMessage;

public interface AsyncTourService {
    void sendToToursZipMultires(TourMessage tourMessage);
    void sendToToursZipEquirectangular(TourMessage tourMessage);
}
