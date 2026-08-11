package io.turismo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.turismo.backend.dto.geojson.GeoFeatureCollectionDTO;
import io.turismo.backend.dto.tourist_spot.*;
import io.turismo.backend.exception.InvalidDateException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.service.TouristSpotService;
import io.turismo.backend.security.CustomOAuth2UserService;
import io.turismo.backend.security.OAuth2AuthenticationSuccessHandler;
import io.turismo.backend.security.TokenService;
import io.turismo.backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TouristSpotController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TouristSpotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private TouristSpotService touristSpotService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockitoBean
    private UserRepository userRepository;

    private UUID touristSpotId;
    private UUID cityId;
    private UUID spotManagerId;

    private TouristSpotDTO touristSpotDTO;
    private TouristSpotListDTO touristSpotListDTO;
    private TouristSpotCreateDTO touristSpotCreateDTO;
    private TouristSpotUpdateDTO touristSpotUpdateDTO;

    @BeforeEach
    void setup() {
        touristSpotId = UUID.randomUUID();
        cityId = UUID.randomUUID();
        spotManagerId = UUID.randomUUID();

        touristSpotDTO = new TouristSpotDTO(
                touristSpotId,
                "Parque Ibirapuera",
                null,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                "Parque urbano de SP",
                "Descrição completa do Parque Ibirapuera",
                null, null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
        );

        touristSpotListDTO = new TouristSpotListDTO(
                touristSpotId,
                "Parque Ibirapuera",
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                "Parque urbano de SP",
                null, Collections.emptyList(), Collections.emptyList(), 1500.0
        );

        touristSpotCreateDTO = new TouristSpotCreateDTO(
                "Parque Ibirapuera",
                -23.550520,
                -46.633308,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                "Parque urbano de SP",
                "Descrição completa do Parque Ibirapuera",
                cityId,
                Set.of("Natureza"),
                Collections.emptyList()
        );

        touristSpotUpdateDTO = new TouristSpotUpdateDTO(
                "Parque Ibirapuera Atualizado",
                -23.550520,
                -46.633308,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                "Parque urbano de SP",
                "Descrição completa do Parque Ibirapuera",
                cityId,
                Set.of("Natureza"),
                Collections.emptyList()
        );
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateTouristSpotAndReturn201Created() throws Exception {
        when(touristSpotService.createTouristSpot(any(TouristSpotCreateDTO.class), any())).thenReturn(touristSpotDTO);

        mockMvc.perform(post("/api/v1/tourist-spots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristSpotCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.touristSpotId").value(touristSpotDTO.touristSpotId().toString()))
                .andExpect(jsonPath("$.name").value(touristSpotDTO.name()))
                .andExpect(jsonPath("$.opensAt").value("08:00:00"))
                .andExpect(jsonPath("$.closesAt").value("18:00:00"))
                .andExpect(jsonPath("$.shortDescription").value(touristSpotDTO.shortDescription()))
                .andExpect(jsonPath("$.description").value(touristSpotDTO.description()));
    }

    @Test
    void shouldGetTouristSpotAndReturn200Ok() throws Exception {
        when(touristSpotService.getTouristSpot(touristSpotId)).thenReturn(touristSpotDTO);

        mockMvc.perform(get("/api/v1/tourist-spots/{touristSpotId}", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.touristSpotId").value(touristSpotDTO.touristSpotId().toString()))
                .andExpect(jsonPath("$.name").value(touristSpotDTO.name()))
                .andExpect(jsonPath("$.opensAt").value("08:00:00"))
                .andExpect(jsonPath("$.closesAt").value("18:00:00"))
                .andExpect(jsonPath("$.shortDescription").value(touristSpotDTO.shortDescription()))
                .andExpect(jsonPath("$.description").value(touristSpotDTO.description()));
    }

    @Test
    void shouldGetTouristSpotsToMapAndReturn200Ok() throws Exception {
        GeoFeatureCollectionDTO<TouristSpotToMapDTO> geoDto = new GeoFeatureCollectionDTO<>("FeatureCollection", Collections.emptyList());
        when(touristSpotService.getTouristSpotsToMap()).thenReturn(geoDto);

        mockMvc.perform(get("/api/v1/spots-to-map/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("FeatureCollection"));
    }

    @Test
    void shouldGetTouristSpotsAndReturn200Ok() throws Exception {
        Page<TouristSpotListDTO> page = new PageImpl<>(List.of(touristSpotListDTO));
        when(touristSpotService.getTouristSpots(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tourist-spots")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].touristSpotId").value(touristSpotListDTO.touristSpotId().toString()))
                .andExpect(jsonPath("$.content[0].name").value(touristSpotListDTO.name()))
                .andExpect(jsonPath("$.content[0].opensAt").value("08:00:00"))
                .andExpect(jsonPath("$.content[0].closesAt").value("18:00:00"))
                .andExpect(jsonPath("$.content[0].shortDescription").value(touristSpotListDTO.shortDescription()));
    }

    @Test
    void shouldGetSpotManagerTouristSpotsAndReturn200Ok() throws Exception {
        Page<TouristSpotListDTO> page = new PageImpl<>(List.of(touristSpotListDTO));
        when(touristSpotService.getSpotManagerTouristSpots(eq(spotManagerId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/manager/{spotManagerId}/all-tourist-spots", spotManagerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].touristSpotId").value(touristSpotListDTO.touristSpotId().toString()))
                .andExpect(jsonPath("$.content[0].name").value(touristSpotListDTO.name()))
                .andExpect(jsonPath("$.content[0].opensAt").value("08:00:00"))
                .andExpect(jsonPath("$.content[0].closesAt").value("18:00:00"))
                .andExpect(jsonPath("$.content[0].shortDescription").value(touristSpotListDTO.shortDescription()));
    }

    @Test
    void shouldUpdateTouristSpotAndReturn200Ok() throws Exception {
        doNothing().when(touristSpotService).updateTouristSpot(eq(touristSpotId), any(TouristSpotUpdateDTO.class), any());

        mockMvc.perform(put("/api/v1/tourist-spots/{touristSpotId}", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristSpotUpdateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteTouristSpotAndReturn204NoContent() throws Exception {
        doNothing().when(touristSpotService).deleteTouristSpot(eq(touristSpotId), any());

        mockMvc.perform(delete("/api/v1/tourist-spots/{touristSpotId}", touristSpotId))
                .andExpect(status().isNoContent());
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateTouristSpotAndReturn400BadRequestWhenDataIsInvalid() throws Exception {
        TouristSpotCreateDTO invalidDTO = new TouristSpotCreateDTO("", null, null, null, null, "", "", null, null, null);

        mockMvc.perform(post("/api/v1/tourist-spots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateTouristSpotAndReturn404NotFoundWhenSpotManagerDoesNotExist() throws Exception {
        when(touristSpotService.createTouristSpot(any(TouristSpotCreateDTO.class), any()))
                .thenThrow(new ObjectNotFoundException("Gerente não encontrado"));

        mockMvc.perform(post("/api/v1/tourist-spots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristSpotCreateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotCreateTouristSpotAndReturn404NotFoundWhenCityDoesNotExist() throws Exception {
        when(touristSpotService.createTouristSpot(any(TouristSpotCreateDTO.class), any()))
                .thenThrow(new ObjectNotFoundException("Cidade não encontrada"));

        mockMvc.perform(post("/api/v1/tourist-spots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristSpotCreateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotCreateTouristSpotAndReturn409ConflictWhenClosingTimeIsBeforeOpeningTime() throws Exception {
        when(touristSpotService.createTouristSpot(any(TouristSpotCreateDTO.class), any()))
                .thenThrow(new InvalidDateException("Horário de fechamento não pode ser antes do horário de abertura"));

        mockMvc.perform(post("/api/v1/tourist-spots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristSpotCreateDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotGetTouristSpotAndReturn404NotFoundWhenSpotDoesNotExist() throws Exception {
        when(touristSpotService.getTouristSpot(touristSpotId))
                .thenThrow(new ObjectNotFoundException("Ponto Turístico não encontrado"));

        mockMvc.perform(get("/api/v1/tourist-spots/{touristSpotId}", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateTouristSpotAndReturn409ConflictWhenClosingTimeIsBeforeOpeningTime() throws Exception {
        doThrow(new InvalidDateException("Horário de fechamento não pode ser antes do horário de abertura"))
                .when(touristSpotService).updateTouristSpot(eq(touristSpotId), any(TouristSpotUpdateDTO.class), any());

        mockMvc.perform(put("/api/v1/tourist-spots/{touristSpotId}", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristSpotUpdateDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotUpdateTouristSpotAndReturn404NotFoundWhenSpotDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Ponto Turístico não encontrado"))
                .when(touristSpotService).updateTouristSpot(eq(touristSpotId), any(TouristSpotUpdateDTO.class), any());

        mockMvc.perform(put("/api/v1/tourist-spots/{touristSpotId}", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristSpotUpdateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateTouristSpotAndReturn401UnauthorizedWhenUserIsNotOwner() throws Exception {
        doThrow(new UserIsNotOwnerException("Você não tem autorização para alterar este ponto turístico"))
                .when(touristSpotService).updateTouristSpot(eq(touristSpotId), any(TouristSpotUpdateDTO.class), any());

        mockMvc.perform(put("/api/v1/tourist-spots/{touristSpotId}", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristSpotUpdateDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotDeleteTouristSpotAndReturn404NotFoundWhenSpotDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Ponto Turístico não encontrado"))
                .when(touristSpotService).deleteTouristSpot(eq(touristSpotId), any());

        mockMvc.perform(delete("/api/v1/tourist-spots/{touristSpotId}", touristSpotId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteTouristSpotAndReturn401UnauthorizedWhenUserIsNotAdminOrOwner() throws Exception {
        doThrow(new UserIsNotAdminOrOwnerException("Você não tem autorização para isso"))
                .when(touristSpotService).deleteTouristSpot(eq(touristSpotId), any());

        mockMvc.perform(delete("/api/v1/tourist-spots/{touristSpotId}", touristSpotId))
                .andExpect(status().isUnauthorized());
    }
}
