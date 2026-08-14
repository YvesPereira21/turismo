package io.turismo.backend.service;

import io.turismo.backend.model.Activity;
import io.turismo.backend.model.Photo;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.dto.photo.PhotoUploadDTO;
import io.turismo.backend.repository.ActivityRepository;
import io.turismo.backend.repository.TouristSpotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class PhotoService {

    private final TouristSpotRepository touristSpotRepository;
    private final ActivityRepository activityRepository;
    @Value("${file.upload-dir.tourist-spot-dir}")
    private String touristSpotUploadDir;
    @Value("${file.upload-dir.activity-dir}")
    private String activityUploadDir;

    public PhotoService(TouristSpotRepository touristSpotRepository, ActivityRepository activityRepository) {
        this.touristSpotRepository = touristSpotRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(rollbackFor = IOException.class)
    public void uploadTouristSpotsPhotos(UUID touristSpotId, PhotoUploadDTO dto) throws IOException {
        log.info("Uploading photo for tourist spot ID: {}", touristSpotId);
        TouristSpot touristSpot = touristSpotRepository.findById(touristSpotId)
                .orElseThrow(() -> new RuntimeException("Ponto turístico não encontrado"));

        try {
            Path path = Paths.get(touristSpotUploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            MultipartFile file = dto.photo();
            if (file == null || file.getContentType() == null || !file.getContentType().startsWith("image/")) {
                throw new RuntimeException("Por favor, insira uma imagem válida.");
            }

            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path filePath = path.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            String fileUrl = touristSpotUploadDir.endsWith("/") ? touristSpotUploadDir + fileName
                    : touristSpotUploadDir + "/" + fileName;

            Photo newPhoto = new Photo();
            newPhoto.setUrl(fileUrl);
            newPhoto.setAltText(dto.altText());
            newPhoto.setTouristSpot(touristSpot);
            touristSpot.getPhotos().add(newPhoto);

        } catch (IOException ex) {
            throw new IOException("Erro ao salvar foto do ponto turístico");
        }
        touristSpotRepository.save(touristSpot);
    }

    @Transactional(rollbackFor = IOException.class)
    public void uploadActivityPhoto(UUID activityId, MultipartFile photo) throws IOException {
        log.info("Uploading photo for activity ID: {}", activityId);
        if (photo == null || photo.getContentType() == null || !photo.getContentType().startsWith("image/")) {
            throw new RuntimeException("Por favor, insira uma imagem válida.");
        }

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Atividade do ponto não encontrada"));

        try {
            Path path = Paths.get(activityUploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String fileName = UUID.randomUUID().toString() + "." + photo.getOriginalFilename();
            Path filePath = path.resolve(fileName);
            Files.copy(photo.getInputStream(), filePath);

            String fileUrl = activityUploadDir.endsWith("/") ? activityUploadDir + fileName
                    : activityUploadDir + "/" + fileName;

            Photo newPhoto = new Photo();
            newPhoto.setUrl(fileUrl);
            newPhoto.setActivity(activity);
            newPhoto.setTouristSpot(activity.getTouristSpot());
            activity.setPhoto(newPhoto);

            activityRepository.save(activity);
        } catch (IOException ex) {
            log.error("Failed to save activity photo: {}", ex.getMessage());
            throw new IOException("Erro ao salvar foto do ponto turístico");
        }
    }

    @Transactional(rollbackFor = IOException.class)
    public void updateActivityPhoto(UUID activityId, MultipartFile photo) throws IOException {
        log.info("Updating photo for activity ID: {}", activityId);
        if (photo == null || photo.getContentType() == null || !photo.getContentType().startsWith("image/")) {
            throw new RuntimeException("Por favor, insira uma imagem válida.");
        }

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));

        try {
            Photo currentPhoto = activity.getPhoto();
            Path oldPath = Paths.get(currentPhoto.getUrl());
            Files.deleteIfExists(oldPath);

            Path dirPath = Paths.get(activityUploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = UUID.randomUUID().toString() + "." + photo.getOriginalFilename();
            Path filePath = dirPath.resolve(fileName);
            Files.copy(photo.getInputStream(), filePath);

            String fileUrl = activityUploadDir.endsWith("/") ? activityUploadDir + fileName
                    : activityUploadDir + "/" + fileName;

            currentPhoto = new Photo();
            currentPhoto.setActivity(activity);
            currentPhoto.setTouristSpot(activity.getTouristSpot());
            currentPhoto.setUrl(fileUrl);
            activity.setPhoto(currentPhoto);
        } catch (IOException ex) {
            throw new IOException("Erro ao salvar foto do ponto turístico");
        }

        activityRepository.save(activity);
    }
}
