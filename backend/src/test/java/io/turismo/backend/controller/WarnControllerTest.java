package io.turismo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.turismo.backend.dto.warn.WarnCreateDTO;
import io.turismo.backend.dto.warn.WarnDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.service.WarnService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WarnController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WarnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private WarnService warnService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockitoBean
    private UserRepository userRepository;

    private UUID warnId;
    private UUID touristSpotId;
    private WarnDTO warnDTO;
    private WarnCreateDTO warnCreateDTO;

    @BeforeEach
    void setup() {
        warnId = UUID.randomUUID();
        touristSpotId = UUID.randomUUID();

        warnDTO = new WarnDTO(
                warnId,
                "Primeiro aviso",
                "Esse é o primeiro aviso já criado",
                LocalDate.now()
        );

        warnCreateDTO = new WarnCreateDTO(
                "Segundo aviso",
                "Esse é o segundo aviso já criado"
        );
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateWarnAndReturn201Created() throws Exception {
        when(warnService.createWarn(any(), any(WarnCreateDTO.class), any())).thenReturn(warnDTO);

        mockMvc.perform(post("/api/v1/warns/spot/{touristSpotId}", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warnCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(warnDTO.id().toString()))
                .andExpect(jsonPath("$.name").value(warnDTO.name()))
                .andExpect(jsonPath("$.description").value(warnDTO.description()))
                .andExpect(jsonPath("$.eventDate").value(warnDTO.eventDate().toString()));
    }

    @Test
    void shouldReturnWarnAndReturn200Ok() throws Exception {
        when(warnService.getWarn(warnId)).thenReturn(warnDTO);

        mockMvc.perform(get("/api/v1/warns/{warnId}", warnId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(warnDTO.id().toString()))
                .andExpect(jsonPath("$.name").value(warnDTO.name()))
                .andExpect(jsonPath("$.description").value(warnDTO.description()))
                .andExpect(jsonPath("$.eventDate").value(warnDTO.eventDate().toString()));
    }

    @Test
    void shouldReturnAllWarnsFromTouristSpot() throws Exception {
        Page<WarnDTO> page = new PageImpl<>(List.of(warnDTO));

        when(warnService.getAllTouristSpotWarn(eq(touristSpotId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/warns/spot/{touristSpotId}", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(warnDTO.id().toString()))
                .andExpect(jsonPath("$.content[0].name").value(warnDTO.name()))
                .andExpect(jsonPath("$.content[0].description").value(warnDTO.description()))
                .andExpect(jsonPath("$.content[0].eventDate").value(warnDTO.eventDate().toString()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(1));
    }

    @Test
    void shouldDeleteWarnAndReturn204() throws Exception {
        doNothing().when(warnService).deleteWarn(any(), eq(warnId));

        mockMvc.perform(delete("/api/v1/warns/{warnId}", warnId))
                .andExpect(status().isNoContent());
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateWarnAndReturn400BadRequestWhenDataIsInvalid() throws Exception {
        WarnCreateDTO invalidDTO = new WarnCreateDTO("", "");

        mockMvc.perform(post("/api/v1/warns/spot/{touristSpotId}", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateWarnAndReturn401UnauthorizedWhenUserIsNotTheOwner() throws Exception {
        when(warnService.createWarn(any(), any(WarnCreateDTO.class), any()))
                .thenThrow(new UserIsNotOwnerException("Você não tem autorização para isso"));

        mockMvc.perform(post("/api/v1/warns/spot/{touristSpotId}", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warnCreateDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotCreateWarnAndReturn404NotFoundWhenTouristSpotDoesNotExist() throws Exception {
        when(warnService.createWarn(any(), any(WarnCreateDTO.class), any()))
                .thenThrow(new ObjectNotFoundException("Ponto Turístico não encontrado"));

        mockMvc.perform(post("/api/v1/warns/spot/{touristSpotId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warnCreateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotReturnWarnAndReturn404NotFoundWhenWarnDoesNotExist() throws Exception {
        UUID randomId = UUID.randomUUID();

        when(warnService.getWarn(randomId))
                .thenThrow(new ObjectNotFoundException("Aviso não encontrado"));

        mockMvc.perform(get("/api/v1/warns/{warnId}", randomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotReturnAllWarnsFromTouristSpotAndReturn404NotFoundWhenTouristSpotDoesNotExist() throws Exception {
        UUID randomId = UUID.randomUUID();

        when(warnService.getAllTouristSpotWarn(eq(randomId), any(Pageable.class)))
                .thenThrow(new ObjectNotFoundException("Ponto Turístico não encontrado"));

        mockMvc.perform(get("/api/v1/warns/spot/{touristSpotId}", randomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteWarnAndReturn404NotFoundWhenWarnDoesNotExist() throws Exception {
        UUID randomId = UUID.randomUUID();

        doThrow(new ObjectNotFoundException("Aviso não encontrado"))
                .when(warnService).deleteWarn(any(), eq(randomId));

        mockMvc.perform(delete("/api/v1/warns/{warnId}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteWarnAndReturn401UnauthorizedWhenUserIsNotTheOwner() throws Exception {
        doThrow(new UserIsNotOwnerException("Você não tem autorização para isso"))
                .when(warnService).deleteWarn(any(), eq(warnId));

        mockMvc.perform(delete("/api/v1/warns/{warnId}", warnId))
                .andExpect(status().isUnauthorized());
    }
}
