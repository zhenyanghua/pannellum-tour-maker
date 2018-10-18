package com.leafyjava.pannellumtourmaker.domains;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.stream.Collectors;

@Document(collection = "tours")
public class Tour {
    @Id
    private String name;
    private String alias;
    private String firstScene;
    private String mapPath;
    private Set<Scene> scenes;
    private String groupName;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
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

    public void deleteScene(String sceneId) {
        scenes = scenes.stream()
            .filter(scene -> !scene.getId().equalsIgnoreCase(sceneId))
            .collect(Collectors.toSet());

        scenes.forEach(scene -> scene.deleteHotSpots(sceneId));
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }
}
