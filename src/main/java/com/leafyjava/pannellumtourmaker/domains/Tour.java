package com.leafyjava.pannellumtourmaker.domains;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "tours")
public class Tour {
    @Id
    private String name;
    private String firstScene;
    private String mapPath;
    private Set<Scene> scenes;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMapPath() {
        return mapPath;
    }

    public void setMapPath(final String mapPath) {
        this.mapPath = mapPath;
    }

    public String getFirstScene() {
        return firstScene;
    }

    public void setFirstScene(final String firstScene) {
        this.firstScene = firstScene;
    }

    public Set<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(final Set<Scene> scenes) {
        this.scenes = scenes;
    }

    public void addScenes(Set<Scene> scenes) {
        this.scenes.addAll(scenes);
    }
}
