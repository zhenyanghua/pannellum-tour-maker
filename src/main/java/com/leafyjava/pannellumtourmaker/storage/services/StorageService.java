package com.leafyjava.pannellumtourmaker.storage.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(String name, MultipartFile file);
    void store(String name, MultipartFile file, Path destination);
    void store(String name, Path file);
    void store(String name, Path file,  Path destination);

    File createTempFileFromMultipartFile(final MultipartFile file);

    void storeTourContent(String name, File file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}
