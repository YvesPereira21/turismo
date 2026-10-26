package io.turismo.backend.controller;

import io.turismo.backend.controller.doc.PhotoDoc;
import io.turismo.backend.dto.photo.PhotoUploadDTO;
import io.turismo.backend.service.PhotoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class PhotoController implements PhotoDoc {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @Override
    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PostMapping(value = "/tourist-spots/{touristSpotId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadTouristSpotsPhotos(
            @PathVariable UUID touristSpotId,
            @ModelAttribute PhotoUploadDTO dto
    ) throws IOException {
        log.info("REST request to upload photo for tourist spot ID: {}", touristSpotId);
        photoService.uploadTouristSpotsPhotos(touristSpotId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PostMapping(value = "/activities/{activityId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadActivityPhoto(
            @PathVariable UUID activityId,
            @RequestParam("photo") MultipartFile photo
    ) throws IOException {
        log.info("REST request to upload photo for activity ID: {}", activityId);
        photoService.uploadActivityPhoto(activityId, photo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PutMapping(value = "/activities/{activityId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateActivityPhoto(
            @PathVariable UUID activityId,
            @RequestParam("photo") MultipartFile photo
    ) throws IOException {
        log.info("REST request to update photo for activity ID: {}", activityId);
        photoService.updateActivityPhoto(activityId, photo);
        return ResponseEntity.ok().build();
    }
}
