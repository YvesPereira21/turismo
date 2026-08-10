package io.turismo.backend.controller;

import io.turismo.backend.dto.photo.PhotoUploadDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.service.PhotoService;
import io.turismo.backend.security.CustomOAuth2UserService;
import io.turismo.backend.security.OAuth2AuthenticationSuccessHandler;
import io.turismo.backend.security.TokenService;
import io.turismo.backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PhotoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PhotoService photoService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockitoBean
    private UserRepository userRepository;

    private UUID touristSpotId;
    private UUID activityId;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setup() {
        touristSpotId = UUID.randomUUID();
        activityId = UUID.randomUUID();
        mockFile = new MockMultipartFile(
                "photo",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy image content".getBytes()
        );
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldUploadTouristSpotsPhotosAndReturn201Created() throws Exception {
        doNothing().when(photoService).uploadTouristSpotsPhotos(eq(touristSpotId), any(PhotoUploadDTO.class));

        mockMvc.perform(multipart("/api/v1/tourist-spots/{touristSpotId}/photos", touristSpotId)
                        .file(mockFile)
                        .param("altText", "Foto de teste"))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUploadActivityPhotoAndReturn201Created() throws Exception {
        doNothing().when(photoService).uploadActivityPhoto(eq(activityId), any());

        mockMvc.perform(multipart("/api/v1/activities/{activityId}/photos", activityId)
                        .file(mockFile))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateActivityPhotoAndReturn200Ok() throws Exception {
        doNothing().when(photoService).updateActivityPhoto(eq(activityId), any());

        MockMultipartHttpServletRequestBuilder builder = multipart("/api/v1/activities/{activityId}/photos", activityId);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder.file(mockFile))
                .andExpect(status().isOk());
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotUploadTouristSpotsPhotosAndReturn404NotFoundWhenSpotDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Ponto Turístico não encontrado"))
                .when(photoService).uploadTouristSpotsPhotos(eq(touristSpotId), any(PhotoUploadDTO.class));

        mockMvc.perform(multipart("/api/v1/tourist-spots/{touristSpotId}/photos", touristSpotId)
                        .file(mockFile)
                        .param("altText", "Foto de teste"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUploadActivityPhotoAndReturn404NotFoundWhenActivityDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Atividade não encontrada"))
                .when(photoService).uploadActivityPhoto(eq(activityId), any());

        mockMvc.perform(multipart("/api/v1/activities/{activityId}/photos", activityId)
                        .file(mockFile))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateActivityPhotoAndReturn404NotFoundWhenActivityDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Atividade não encontrada"))
                .when(photoService).updateActivityPhoto(eq(activityId), any());

        MockMultipartHttpServletRequestBuilder builder = multipart("/api/v1/activities/{activityId}/photos", activityId);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder.file(mockFile))
                .andExpect(status().isNotFound());
    }
}
