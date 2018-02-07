package com.leafyjava.pannellumtourmaker.storage.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;

@ConfigurationProperties("storage")
public class StorageProperties {

    @Value("${application.upload.directory}")
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getTourLocation() {
        return Paths.get(location).resolve("tours").toString();
    }

    public String getEquirectangularLocation() {
        return Paths.get(location).resolve("equirectangular").toString();
    }
}
