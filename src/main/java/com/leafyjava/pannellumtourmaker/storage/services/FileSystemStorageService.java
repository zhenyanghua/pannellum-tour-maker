package com.leafyjava.pannellumtourmaker.storage.services;

import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import com.leafyjava.pannellumtourmaker.storage.exceptions.StorageException;
import com.leafyjava.pannellumtourmaker.storage.exceptions.StorageFileNotFoundException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final Path extractedLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties storageProperties) {
        this.rootLocation = Paths.get(storageProperties.getLocation());
        this.extractedLocation = Paths.get(storageProperties.getTourLocation());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(final String name, final MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = StringUtils.cleanPath(name + "." + extension);

        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }

            if (filename.contains("..")) {
                throw new StorageException("Cannot store file with relative path outside current directory " + filename);
            }

            Files.copy(file.getInputStream(), rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public void storeZipContent(final String name, final MultipartFile file) {
        try {
            File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
            FileOutputStream outputStream = new FileOutputStream(zip);
            IOUtils.copy(file.getInputStream(), outputStream);
            outputStream.close();

            try {
                ZipFile zipFile = new ZipFile(zip);
                Path destination = extractedLocation.resolve(name);
                zipFile.extractAll(destination.toString());
                FileSystemUtils.deleteRecursively(destination.resolve("__MACOSX").toFile());

            } catch (ZipException e) {
                throw new StorageException("Fail to save zip file", e);
            } finally {
                zip.delete();
            }

        } catch (IOException e) {
            throw new StorageException("Fail to create a temporary space to save the zip file", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(rootLocation, 1)
                .filter(path -> !path.equals(extractedLocation))
                .filter(path -> !path.equals(rootLocation))
                .map(rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(final String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(final String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
