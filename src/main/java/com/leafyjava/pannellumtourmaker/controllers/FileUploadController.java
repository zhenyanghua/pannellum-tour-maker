package com.leafyjava.pannellumtourmaker.controllers;

import com.leafyjava.pannellumtourmaker.domains.Tour;
import com.leafyjava.pannellumtourmaker.domains.TourMessage;
import com.leafyjava.pannellumtourmaker.domains.UploadedFile;
import com.leafyjava.pannellumtourmaker.exceptions.InvalidTourException;
import com.leafyjava.pannellumtourmaker.exceptions.TourAlreadyExistsException;
import com.leafyjava.pannellumtourmaker.exceptions.UnsupportedFileExtensionException;
import com.leafyjava.pannellumtourmaker.services.AsyncTourService;
import com.leafyjava.pannellumtourmaker.services.FileUploadService;
import com.leafyjava.pannellumtourmaker.services.TourService;

import com.leafyjava.pannellumtourmaker.utils.SupportedTourUploadType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static com.leafyjava.pannellumtourmaker.utils.SupportedTourUploadType.EQUIRECTANGULAR;
import static com.leafyjava.pannellumtourmaker.utils.SupportedTourUploadType.MULTIRES;

@RestController
@RequestMapping("/api/v1/public/guest/file-upload")
public class FileUploadController {

    private FileUploadService fileUploadService;
    private TourService tourService;
    private AsyncTourService asyncTourService;

    @Autowired
    public FileUploadController(final FileUploadService fileUploadService,
                                final TourService tourService,
                                final AsyncTourService asyncTourService) {
        this.fileUploadService = fileUploadService;
        this.tourService = tourService;
        this.asyncTourService = asyncTourService;
    }

    @GetMapping("")
    public List<UploadedFile> getUploadedFiles() {
        return fileUploadService.getUploadedFiles();
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource resource = fileUploadService.loadAsResource(filename);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    @PostMapping("/tours")
    public void uploadFile(@RequestParam("name") String name,
                           @RequestParam("type") String type,
                           @RequestParam("file") MultipartFile file) {
        if (tourService.findOne(name) != null) {
            throw new TourAlreadyExistsException("Tour " + name + " already exists.");
        }
        SupportedTourUploadType tourType = null;
        try{
            tourType = SupportedTourUploadType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidTourException("Tour type is not supported.");
        }
        if (!StringUtils.getFilenameExtension(file.getOriginalFilename()).equalsIgnoreCase("zip")) {
            throw new UnsupportedFileExtensionException("The uploaded file must be a zip file.");
        }

        File zip = fileUploadService.convertToFile(file);
        TourMessage tourMessage = new TourMessage(name, zip);

        switch(tourType) {
            case MULTIRES:
                asyncTourService.sendToToursZipMultires(tourMessage);
                break;
            case EQUIRECTANGULAR:
                asyncTourService.sendToToursZipEquirectangular(tourMessage);
                break;
        }
    }

    @GetMapping("/tours")
    public List<Tour> getTours() {
        return tourService.findAllTours();
    }

    @GetMapping("/tours/{name}")
    public Tour getTourByName(@PathVariable(value = "name") String name) {
        return tourService.findOne(name);
    }

    @PutMapping("/tours/{name}")
    public Tour saveTourByName(@PathVariable(value = "name") String name, @RequestBody Tour tour) {
        if (!name.equalsIgnoreCase(tour.getName()))
            throw new InvalidTourException("Tour name must match in the path and the request body.");

        return tourService.save(tour);
    }
}
