package com.leafyjava.pannellumtourmaker.domains;

import java.io.File;

public class TourMessage {
    private String name;
    private File tourFile;
    private File mapFile;

    public TourMessage() {
    }

    public TourMessage(final String name, final File tourFile) {
        this.name = name;
        this.tourFile = tourFile;
    }

    public TourMessage(final String name, final File tourFile, final File mapFile) {
        this.name = name;
        this.tourFile = tourFile;
        this.mapFile = mapFile;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public File getTourFile() {
        return tourFile;
    }

    public void setTourFile(final File tourFile) {
        this.tourFile = tourFile;
    }

    public File getMapFile() {
        return mapFile;
    }

    public void setMapFile(final File mapFile) {
        this.mapFile = mapFile;
    }
}
