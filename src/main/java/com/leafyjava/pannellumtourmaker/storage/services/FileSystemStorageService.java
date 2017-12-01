package com.leafyjava.pannellumtourmaker.storage.services;

import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import com.leafyjava.pannellumtourmaker.storage.exceptions.StorageException;
import com.leafyjava.pannellumtourmaker.storage.exceptions.StorageFileNotFoundException;
import com.leafyjava.pannellumtourmaker.utils.SupportedTourUploadType;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FilenameUtils;
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
    private final Path tourLocation;
    private final Path equirectangularLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties storageProperties) {
        this.rootLocation = Paths.get(storageProperties.getLocation());
        this.tourLocation = Paths.get(storageProperties.getTourLocation());
        this.equirectangularLocation = Paths.get(storageProperties.getEquirectangularLocation());
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
        String filename = getNewFileName(name, file.getOriginalFilename());

        store(name, file, rootLocation.resolve(filename));
    }

    @Override
    public void store(final String name, final MultipartFile file, final Path destination) {
        String filename = getNewFileName(name, file.getOriginalFilename());

        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }

            if (filename.contains("..")) {
                throw new StorageException("Cannot store file with relative path outside current directory " + filename);
            }

            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public void store(final String name, Path file) {
        store(name, file, rootLocation.resolve(name));
    }

    @Override
    public void store(final String name, final Path file, final Path destination) {
        try {
            Files.createDirectories(destination.getParent());
            Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file, e);
        } finally {
            file.toFile().delete();
        }
    }


    @Override
    public File createTempFileFromMultipartFile(final MultipartFile multipartFilefile) {
        try {
            String extension = "." + FilenameUtils.getExtension(multipartFilefile.getOriginalFilename());
            File file = File.createTempFile(UUID.randomUUID().toString(), extension);
            FileOutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(multipartFilefile.getInputStream(), outputStream);
            outputStream.close();

            return file;

        } catch (IOException e) {
            throw new StorageException("Fail to create a temporary space to save the zip file", e);
        }
    }

    @Override
    public void storeTourContent(final String name, final SupportedTourUploadType type, final File file) {
        try {
            Path destination = null;
            switch (type) {
                case MULTIRES:
                    destination = tourLocation.resolve(name);
                    break;
                case EQUIRECTANGULAR:
                    destination = equirectangularLocation.resolve(name);
                    break;
            }
            ZipFile zipFile = new ZipFile(file);
            zipFile.extractAll(destination.toString());
            FileSystemUtils.deleteRecursively(destination.resolve("__MACOSX").toFile());

        } catch (ZipException e) {
            throw new StorageException("Fail to save zip file", e);
        } finally {
            file.delete();
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(rootLocation, 1)
                .filter(path -> !path.equals(tourLocation))
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

    private String getNewFileName(final String name, final String originalFileNameWithExtension) {
        String extension = StringUtils.getFilenameExtension(originalFileNameWithExtension);
        return StringUtils.cleanPath(name + "." + extension);
    }

}
