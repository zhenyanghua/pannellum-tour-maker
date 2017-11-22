package com.leafyjava.pannellumtourmaker.domains;

public class UploadedFile {
    private String name;
    private String url;

    public UploadedFile() {
    }

    public UploadedFile(final String name, final String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
