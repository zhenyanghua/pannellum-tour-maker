package com.leafyjava.pannellumtourmaker.domains;

public class MultiRes {
    private String id;
    private String basePath;
    private String path;
    private String fallbackPath;
    private String extension;
    private int tileResolution;
    private int maxLevel;
    private int cubeResolution;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getFallbackPath() {
        return fallbackPath;
    }

    public void setFallbackPath(final String fallbackPath) {
        this.fallbackPath = fallbackPath;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(final String extension) {
        this.extension = extension;
    }

    public int getTileResolution() {
        return tileResolution;
    }

    public void setTileResolution(final int tileResolution) {
        this.tileResolution = tileResolution;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(final int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getCubeResolution() {
        return cubeResolution;
    }

    public void setCubeResolution(final int cubeResolution) {
        this.cubeResolution = cubeResolution;
    }
}
