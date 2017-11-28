package com.leafyjava.pannellumtourmaker.domains;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

public class Scene {
    @Id
    private String id;
    private String title;
    private float pitch;
    private float yaw;
    private Exif exif;
    private String type;
    private MultiRes multiRes;
    private List<HotSpot> hotSpots;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }

    public Exif getExif() {
        return exif;
    }

    public void setExif(final Exif exif) {
        this.exif = exif;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public MultiRes getMultiRes() {
        return multiRes;
    }

    public void setMultiRes(final MultiRes multiRes) {
        this.multiRes = multiRes;
    }

    public List<HotSpot> getHotSpots() {
        return hotSpots;
    }

    public void setHotSpots(final List<HotSpot> hotSpots) {
        this.hotSpots = hotSpots;
    }
}
