package com.leafyjava.pannellumtourmaker.domains;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class MultiRes {
//    @Id
//    private ObjectId multiResId;
    private String basePath;
    private String path;
    private String fallbackPath;
    private String extension;
    private int tileResolution;
    private int maxLevel;
    private int cubeResolution;
//
//    public MultiRes() {
//        this.multiResId = ObjectId.get();
//    }
//
//    public ObjectId getMultiResId() {
//        return multiResId;
//    }
//
//    public void setMultiResId(final ObjectId multiResId) {
//        this.multiResId = multiResId;
//    }

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
