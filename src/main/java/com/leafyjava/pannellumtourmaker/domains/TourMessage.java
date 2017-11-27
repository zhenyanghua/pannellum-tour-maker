package com.leafyjava.pannellumtourmaker.domains;

import java.io.File;

public class TourMessage {
    private String name;
    private File file;

    public TourMessage() {
    }

    public TourMessage(final String name, final File file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(final File file) {
        this.file = file;
    }
}
