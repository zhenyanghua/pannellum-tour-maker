package com.leafyjava.pannellumtourmaker.storage.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    @Value("${application.upload.directory}")
    private String location;

    @Value("${application.upload.tours}")
    private String tourLocation;

    @Value("${application.upload.equirectangular}")
    private String equirectangularLocation;


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

    public String getEquirectangularLocation() {
        return equirectangularLocation;
    }

    public void setEquirectangularLocation(final String equirectangularLocation) {
        this.equirectangularLocation = equirectangularLocation;
    }
}
