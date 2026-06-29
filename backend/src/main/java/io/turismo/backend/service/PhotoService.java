package io.turismo.backend.service;

import io.turismo.backend.model.Activity;
import io.turismo.backend.model.Photo;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.repository.ActivityRepository;
import io.turismo.backend.repository.TouristSpotRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

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
    public void uploadTouristSpotsPhotos(UUID touristSpotId, List<MultipartFile> photos) throws IOException {
        TouristSpot touristSpot = touristSpotRepository.findById(touristSpotId)
                .orElseThrow(() -> new RuntimeException("Ponto turístico não encontrado"));

        try {
            Path path = Paths.get(touristSpotUploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            for (MultipartFile photo : photos) {
                if (photo == null || photo.getContentType() == null || !photo.getContentType().startsWith("image/")) {
                    throw new RuntimeException("Por favor, insira uma imagem válida.");
                }

                String fileName = UUID.randomUUID().toString() + "-" + photo.getOriginalFilename();
                Path filePath = path.resolve(fileName);
                Files.copy(photo.getInputStream(), filePath);

                String fileUrl = touristSpotUploadDir.endsWith("/") ? touristSpotUploadDir + fileName
                                : touristSpotUploadDir + "/" + fileName;

                Photo newPhoto = new Photo();
                newPhoto.setUrl(fileUrl);
                newPhoto.setTouristSpot(touristSpot);
                touristSpot.getPhotos().add(newPhoto);
            }
        } catch (IOException ex){
            throw new IOException("Erro ao salvar foto do ponto turístico");
        }
        touristSpotRepository.save(touristSpot);
    }

    @Transactional(rollbackFor = IOException.class)
    public void uploadActivityPhoto(UUID activityId, MultipartFile photo) throws IOException {
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
            activity.setPhoto(newPhoto);

            activityRepository.save(activity);
        } catch (IOException ex){
            throw new IOException("Erro ao salvar foto do ponto turístico");
        }
    }

    @Transactional(rollbackFor = IOException.class)
    public void updateActivityPhoto(UUID activityId, MultipartFile photo) throws IOException {
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
            currentPhoto.setUrl(fileUrl);
            activity.setPhoto(currentPhoto);
        } catch (IOException ex){
            throw new IOException("Erro ao salvar foto do ponto turístico");
        }

        activityRepository.save(activity);
    }
}
