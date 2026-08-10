package io.turismo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import io.turismo.backend.dto.social_media.SocialMediaDTO;
import io.turismo.backend.dto.social_media.SocialMediaUpdateDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.model.enums.SocialMediaType;
import io.turismo.backend.service.SocialMediaService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SocialMediaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SocialMediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private SocialMediaService socialMediaService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockitoBean
    private UserRepository userRepository;

    private UUID touristSpotId;
    private UUID socialMediaId;
    private SocialMediaDTO socialMediaDTO;
    private SocialMediaCreateDTO socialMediaCreateDTO;
    private SocialMediaUpdateDTO socialMediaUpdateDTO;

    @BeforeEach
    void setup() {
        touristSpotId = UUID.randomUUID();
        socialMediaId = UUID.randomUUID();
        socialMediaDTO = new SocialMediaDTO(socialMediaId, "https://instagram.com/ponto", SocialMediaType.INSTAGRAM);
        socialMediaCreateDTO = new SocialMediaCreateDTO("https://instagram.com/ponto", SocialMediaType.INSTAGRAM);
        socialMediaUpdateDTO = new SocialMediaUpdateDTO("https://instagram.com/pontousa");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateSocialMediaAndReturn201Created() throws Exception {
        when(socialMediaService.createSocialMedia(any(SocialMediaCreateDTO.class), eq(touristSpotId))).thenReturn(socialMediaDTO);

        mockMvc.perform(post("/api/v1/tourist-spot/{touristSpotId}/social-medias", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(socialMediaCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.socialMediaId").value(socialMediaDTO.socialMediaId().toString()))
                .andExpect(jsonPath("$.socialMediaLink").value(socialMediaDTO.socialMediaLink()))
                .andExpect(jsonPath("$.socialMediaType").value(socialMediaDTO.socialMediaType().toString()));
    }

    @Test
    void shouldGetAllTouristSpotSocialsMediaAndReturn200Ok() throws Exception {
        when(socialMediaService.getAllTouristSpotSocialsMedia(touristSpotId)).thenReturn(List.of(socialMediaDTO));

        mockMvc.perform(get("/api/v1/tourist-spot/{touristSpotId}/social-medias", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].socialMediaId").value(socialMediaDTO.socialMediaId().toString()))
                .andExpect(jsonPath("$[0].socialMediaLink").value(socialMediaDTO.socialMediaLink()))
                .andExpect(jsonPath("$[0].socialMediaType").value(socialMediaDTO.socialMediaType().toString()));
    }

    @Test
    void shouldUpdateSocialMediaAndReturn200Ok() throws Exception {
        when(socialMediaService.updateSocialMedia(any(SocialMediaUpdateDTO.class), eq(socialMediaId))).thenReturn(socialMediaDTO);

        mockMvc.perform(put("/api/v1/social-medias/{socialMediaId}", socialMediaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(socialMediaUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.socialMediaId").value(socialMediaDTO.socialMediaId().toString()))
                .andExpect(jsonPath("$.socialMediaLink").value(socialMediaDTO.socialMediaLink()))
                .andExpect(jsonPath("$.socialMediaType").value(socialMediaDTO.socialMediaType().toString()));
    }

    @Test
    void shouldDeleteSocialMediaAndReturn204NoContent() throws Exception {
        doNothing().when(socialMediaService).deleteSocialMedia(socialMediaId);

        mockMvc.perform(delete("/api/v1/social-medias/{socialMediaId}", socialMediaId))
                .andExpect(status().isNoContent());
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateSocialMediaAndReturn400BadRequestWhenDataIsInvalid() throws Exception {
        SocialMediaCreateDTO invalidDTO = new SocialMediaCreateDTO("", null);

        mockMvc.perform(post("/api/v1/tourist-spot/{touristSpotId}/social-medias", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateSocialMediaAndReturn404NotFoundWhenTouristSpotDoesNotExist() throws Exception {
        when(socialMediaService.createSocialMedia(any(SocialMediaCreateDTO.class), eq(touristSpotId)))
                .thenThrow(new ObjectNotFoundException("Ponto Turístico não encontrado"));

        mockMvc.perform(post("/api/v1/tourist-spot/{touristSpotId}/social-medias", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(socialMediaCreateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateSocialMediaAndReturn404NotFoundWhenSocialMediaDoesNotExist() throws Exception {
        when(socialMediaService.updateSocialMedia(any(SocialMediaUpdateDTO.class), eq(socialMediaId)))
                .thenThrow(new ObjectNotFoundException("Rede Social não encontrada"));

        mockMvc.perform(put("/api/v1/social-medias/{socialMediaId}", socialMediaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(socialMediaUpdateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteSocialMediaAndReturn404NotFoundWhenSocialMediaDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Rede Social não encontrada"))
                .when(socialMediaService).deleteSocialMedia(socialMediaId);

        mockMvc.perform(delete("/api/v1/social-medias/{socialMediaId}", socialMediaId))
                .andExpect(status().isNotFound());
    }
}
