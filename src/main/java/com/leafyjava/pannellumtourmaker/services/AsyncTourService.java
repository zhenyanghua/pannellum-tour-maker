package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.TourMessage;

public interface AsyncTourService {
    void sendToToursZipEquirectangular(TourMessage tourMessage);
}
