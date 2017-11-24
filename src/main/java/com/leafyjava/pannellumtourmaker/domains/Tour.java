package com.leafyjava.pannellumtourmaker.domains;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "tours")
public class Tour {
    @Id
    private String name;
    private List<Scene> scenes;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(final List<Scene> scenes) {
        this.scenes = scenes;
    }
}
