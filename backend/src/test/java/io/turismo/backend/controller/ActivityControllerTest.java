package io.turismo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turismo.backend.dto.activity.ActivityCreateDTO;
import io.turismo.backend.dto.activity.ActivityDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.repository.UserRepository;
import io.turismo.backend.security.CustomOAuth2UserService;
import io.turismo.backend.security.OAuth2AuthenticationSuccessHandler;
import io.turismo.backend.security.TokenService;
import io.turismo.backend.service.ActivityService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ActivityService activityService;

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
    private UUID userId;
    private ActivityDTO activityDTO;
    private ActivityCreateDTO activityCreateDTO;

    @BeforeEach
    void setup() {
        touristSpotId = UUID.randomUUID();
        activityId = UUID.randomUUID();
        userId = UUID.randomUUID();

        activityDTO = new ActivityDTO(activityId, "Trilha Ecológica", null);
        activityCreateDTO = new ActivityCreateDTO("Trilha Ecológica");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateActivityAndReturn201Created() throws Exception {
        when(activityService.createActivity(eq(touristSpotId), any(), any(ActivityCreateDTO.class)))
                .thenReturn(activityDTO);

        mockMvc.perform(post("/api/v1/tourist-spots/{touristSpotId}/activities", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activityCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.activityId").value(activityDTO.activityId().toString()))
                .andExpect(jsonPath("$.name").value(activityDTO.name()));
    }

    @Test
    void shouldGetActivitiesByTouristSpotIdAndReturn200Ok() throws Exception {
        when(activityService.getActivitiesByTouristSpotId(touristSpotId))
                .thenReturn(Set.of(activityDTO));

        mockMvc.perform(get("/api/v1/tourist-spots/{touristSpotId}/activities", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].activityId").value(activityDTO.activityId().toString()))
                .andExpect(jsonPath("$[0].name").value(activityDTO.name()));
    }

    @Test
    void shouldUpdateActivityAndReturn200Ok() throws Exception {
        doNothing().when(activityService).updateActivity(eq(activityId), any(), any(ActivityCreateDTO.class));

        mockMvc.perform(put("/api/v1/activities/{activityId}", activityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activityCreateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteActivityAndReturn204NoContent() throws Exception {
        doNothing().when(activityService).deleteActivity(eq(activityId), any());

        mockMvc.perform(delete("/api/v1/activities/{activityId}", activityId))
                .andExpect(status().isNoContent());
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateActivityAndReturn400BadRequestWhenDataIsInvalid() throws Exception {
        ActivityCreateDTO invalidDTO = new ActivityCreateDTO("");

        mockMvc.perform(post("/api/v1/tourist-spots/{touristSpotId}/activities", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateActivityAndReturn404NotFoundWhenTouristSpotDoesNotExist() throws Exception {
        when(activityService.createActivity(eq(touristSpotId), any(), any(ActivityCreateDTO.class)))
                .thenThrow(new ObjectNotFoundException("Ponto turístico não encontrado"));

        mockMvc.perform(post("/api/v1/tourist-spots/{touristSpotId}/activities", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activityCreateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotGetActivitiesByTouristSpotIdAndReturn404NotFoundWhenTouristSpotDoesNotExist() throws Exception {
        when(activityService.getActivitiesByTouristSpotId(touristSpotId))
                .thenThrow(new ObjectNotFoundException("Ponto turístico não encontrado"));

        mockMvc.perform(get("/api/v1/tourist-spots/{touristSpotId}/activities", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateActivityAndReturn404NotFoundWhenActivityDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Atividade não encontrada"))
                .when(activityService).updateActivity(eq(activityId), any(), any(ActivityCreateDTO.class));

        mockMvc.perform(put("/api/v1/activities/{activityId}", activityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activityCreateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteActivityAndReturn404NotFoundWhenActivityDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Atividade não encontrada"))
                .when(activityService).deleteActivity(eq(activityId), any());

        mockMvc.perform(delete("/api/v1/activities/{activityId}", activityId))
                .andExpect(status().isNotFound());
    }
}
