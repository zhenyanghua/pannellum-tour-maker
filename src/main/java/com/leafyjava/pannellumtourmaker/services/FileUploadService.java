package com.leafyjava.pannellumtourmaker.services;

import com.leafyjava.pannellumtourmaker.domains.UploadedFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileUploadService {
    List<UploadedFile> getUploadedFiles();

    Resource loadAsResource(String filename);

    void store(String name, File file);

    File convertToFile(MultipartFile file);
}
