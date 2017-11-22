package com.leafyjava.pannellumtourmaker.storage.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    private String location = "upload-dir";
    private String extractedLocation = location + "/extracted";

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getExtractedLocation() {
        return extractedLocation;
    }

    public void setExtractedLocation(final String extractedLocation) {
        this.extractedLocation = extractedLocation;
    }
}
