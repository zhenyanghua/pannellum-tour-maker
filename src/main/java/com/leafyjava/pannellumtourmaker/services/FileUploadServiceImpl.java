package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.controllers.FileUploadController;
import com.leafyjava.pannellumtourmaker.domains.UploadedFile;
import com.leafyjava.pannellumtourmaker.exceptions.ExceptionResponse;
import com.leafyjava.pannellumtourmaker.storage.exceptions.StorageFileNotFoundException;
import com.leafyjava.pannellumtourmaker.storage.services.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final StorageService storageService;

    @Autowired
    public FileUploadServiceImpl(final StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public List<UploadedFile> getUploadedFiles() {
        return storageService.loadAll()
            .map(path -> new UploadedFile(
                FilenameUtils.removeExtension(path.getFileName().toString()),
                MvcUriComponentsBuilder.fromMethodName(
                    FileUploadController.class,
                    "serveFile",
                    path.toString()).build().toString()
            ))
            .collect(Collectors.toList());
    }


    @Override
    public Resource loadAsResource(final String filename) {
        return storageService.loadAsResource(filename);
    }

    @Override
    public UploadedFile store(final String name, final MultipartFile file) {
        storageService.storeZipContent(name, file);

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = StringUtils.cleanPath(name + "." + extension);

        Path path = storageService.load(filename);
        return new UploadedFile(
            name,
            MvcUriComponentsBuilder.fromMethodName(
                FileUploadController.class,
                "serveFile",
                path.getFileName().toString())
                .build().toString());
    }
}
