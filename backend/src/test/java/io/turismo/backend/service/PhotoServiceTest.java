package io.turismo.backend.service;

import io.turismo.backend.dto.photo.PhotoUploadDTO;
import io.turismo.backend.model.Activity;
import io.turismo.backend.model.Photo;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.repository.ActivityRepository;
import io.turismo.backend.repository.TouristSpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private TouristSpotRepository touristSpotRepository;
    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private PhotoService photoService;

    private TouristSpot touristSpot;
    private Activity activity;
    private MockMultipartFile mockFile;
    private PhotoUploadDTO photoUploadDTO;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        ReflectionTestUtils.setField(photoService, "touristSpotUploadDir", tempDir.resolve("tourist_spots").toString());
        ReflectionTestUtils.setField(photoService, "activityUploadDir", tempDir.resolve("activities").toString());

        touristSpot = new TouristSpot();
        ReflectionTestUtils.setField(touristSpot, "touristSpotId", UUID.randomUUID());
        touristSpot.setPhotos(new ArrayList<>());

        activity = new Activity();
        ReflectionTestUtils.setField(activity, "activityId", UUID.randomUUID());

        mockFile = new MockMultipartFile("photo", "test.jpg", "image/jpeg", "image content".getBytes());
        photoUploadDTO = new PhotoUploadDTO(mockFile, "Alt text");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldUploadTouristSpotsPhotos() throws IOException {
        UUID spotId = touristSpot.getTouristSpotId();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.of(touristSpot));
        when(touristSpotRepository.save(any(TouristSpot.class))).thenReturn(touristSpot);

        assertDoesNotThrow(() -> photoService.uploadTouristSpotsPhotos(spotId, photoUploadDTO));

        verify(touristSpotRepository, times(1)).findById(spotId);
        verify(touristSpotRepository, times(1)).save(any(TouristSpot.class));
    }

    @Test
    void shouldUploadActivityPhoto() throws IOException {
        UUID activityId = activity.getActivityId();

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        assertDoesNotThrow(() -> photoService.uploadActivityPhoto(activityId, mockFile));

        verify(activityRepository, times(1)).findById(activityId);
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void shouldUpdateActivityPhoto() throws IOException {
        UUID activityId = activity.getActivityId();
        Photo photo = new Photo();
        photo.setUrl("/tmp/old_file.jpg");
        activity.setPhoto(photo);

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        assertDoesNotThrow(() -> photoService.updateActivityPhoto(activityId, mockFile));

        verify(activityRepository, times(1)).findById(activityId);
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotUploadTouristSpotsPhotosAndThrowExceptionWhenSpotNotFound() {
        UUID spotId = UUID.randomUUID();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> photoService.uploadTouristSpotsPhotos(spotId, photoUploadDTO)
        );

        verify(touristSpotRepository, times(1)).findById(spotId);
        verify(touristSpotRepository, never()).save(any(TouristSpot.class));
    }

    @Test
    void shouldNotUploadTouristSpotsPhotosAndThrowExceptionWhenInvalidFile() {
        UUID spotId = touristSpot.getTouristSpotId();
        MockMultipartFile invalidFile = new MockMultipartFile("photo", "test.txt", "text/plain", "data".getBytes());
        PhotoUploadDTO invalidDto = new PhotoUploadDTO(invalidFile, "Alt text");

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.of(touristSpot));

        assertThrows(
                RuntimeException.class,
                () -> photoService.uploadTouristSpotsPhotos(spotId, invalidDto)
        );

        verify(touristSpotRepository, times(1)).findById(spotId);
        verify(touristSpotRepository, never()).save(any(TouristSpot.class));
    }

    @Test
    void shouldNotUploadActivityPhotoAndThrowExceptionWhenInvalidFile() {
        UUID activityId = activity.getActivityId();
        MockMultipartFile invalidFile = new MockMultipartFile("photo", "test.txt", "text/plain", "data".getBytes());

        assertThrows(
                RuntimeException.class,
                () -> photoService.uploadActivityPhoto(activityId, invalidFile)
        );

        verify(activityRepository, never()).findById(any());
        verify(activityRepository, never()).save(any());
    }

    @Test
    void shouldNotUploadActivityPhotoAndThrowExceptionWhenActivityNotFound() {
        UUID activityId = UUID.randomUUID();

        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> photoService.uploadActivityPhoto(activityId, mockFile)
        );

        verify(activityRepository, times(1)).findById(activityId);
        verify(activityRepository, never()).save(any());
    }

    @Test
    void shouldNotUpdateActivityPhotoAndThrowExceptionWhenActivityNotFound() {
        UUID activityId = UUID.randomUUID();

        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> photoService.updateActivityPhoto(activityId, mockFile)
        );

        verify(activityRepository, times(1)).findById(activityId);
        verify(activityRepository, never()).save(any());
    }
}
