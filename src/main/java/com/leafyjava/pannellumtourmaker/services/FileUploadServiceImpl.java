package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.controllers.FileUploadController;
import com.leafyjava.pannellumtourmaker.domains.UploadedFile;
import com.leafyjava.pannellumtourmaker.storage.services.StorageService;
import com.leafyjava.pannellumtourmaker.utils.SupportedTourUploadType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.File;
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
    public void store(final String name, final SupportedTourUploadType type, final File file) {
        storageService.storeZipContent(name, type, file);
    }

    @Override
    public File convertToFile(final MultipartFile file) {
        return storageService.convertToFile(file);
    }
}
