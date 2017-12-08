package com.leafyjava.pannellumtourmaker.domains;

import java.io.File;

public class TourMessage {
    private String name;
    private int northOffset;
    private File tourFile;
    private File mapFile;
    private Task task;

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

    public int getNorthOffset() {
        return northOffset;
    }

    public void setNorthOffset(final int northOffset) {
        this.northOffset = northOffset;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(final Task task) {
        this.task = task;
    }
}
