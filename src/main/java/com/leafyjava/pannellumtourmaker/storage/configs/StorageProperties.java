package com.leafyjava.pannellumtourmaker.storage.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    @Value("${upload.directory}")
    private String location;

    @Value("${upload.tours}")
    private String tourLocation;

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getTourLocation() {
        return tourLocation;
    }

    public void setTourLocation(final String tourLocation) {
        this.tourLocation = tourLocation;
    }
}
