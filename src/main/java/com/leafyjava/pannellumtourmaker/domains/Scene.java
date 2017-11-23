package com.leafyjava.pannellumtourmaker.domains;

import java.util.List;

public class Scene {
    private String title;
    private float pitch;
    private float yaw;
    private String type;
    private MultiRes multiRes;
    private List<HotSpot> hotSpots;

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
