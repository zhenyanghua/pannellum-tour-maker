package com.leafyjava.pannellumtourmaker.domains;

import org.springframework.data.annotation.Id;

public class HotSpot {
    @Id
    private String id;
    private float pitch;
    private float yaw;
    private String type;
    private String text;
    private String sceneId;
    private float targetYaw;
    private float targetPitch;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
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

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(final String sceneId) {
        this.sceneId = sceneId;
    }

    public float getTargetYaw() {
        return targetYaw;
    }

    public void setTargetYaw(final float targetYaw) {
        this.targetYaw = targetYaw;
    }

    public float getTargetPitch() {
        return targetPitch;
    }

    public void setTargetPitch(final float targetPitch) {
        this.targetPitch = targetPitch;
    }
}
