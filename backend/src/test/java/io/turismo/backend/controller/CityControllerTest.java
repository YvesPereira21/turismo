package io.turismo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turismo.backend.dto.city.CityCreateDTO;
import io.turismo.backend.dto.city.CityDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.service.CityService;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CityController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CityService cityService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockitoBean
    private UserRepository userRepository;

    private UUID cityId;
    private CityDTO cityDTO;
    private CityCreateDTO cityCreateDTO;

    @BeforeEach
    void setup() {
        cityId = UUID.randomUUID();
        cityDTO = new CityDTO(cityId, "São Paulo", "São Paulo");
        cityCreateDTO = new CityCreateDTO("São Paulo", "São Paulo");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateCityAndReturn201Created() throws Exception {
        when(cityService.createCity(any(CityCreateDTO.class))).thenReturn(cityDTO);

        mockMvc.perform(post("/api/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cityId").value(cityDTO.cityId().toString()))
                .andExpect(jsonPath("$.name").value(cityDTO.name()))
                .andExpect(jsonPath("$.stateName").value(cityDTO.stateName()));
    }

    @Test
    void shouldGetCityAndReturn200Ok() throws Exception {
        when(cityService.getCity("São Paulo", "São Paulo")).thenReturn(cityDTO);

        mockMvc.perform(get("/api/v1/cities/{cityName}/{stateName}", "São Paulo", "São Paulo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cityId").value(cityDTO.cityId().toString()))
                .andExpect(jsonPath("$.name").value(cityDTO.name()))
                .andExpect(jsonPath("$.stateName").value(cityDTO.stateName()));
    }

    @Test
    void shouldGetCitiesFromStateAndReturn200Ok() throws Exception {
        when(cityService.getCitiesFromState(eq("São Paulo"))).thenReturn(List.of(cityDTO));

        mockMvc.perform(get("/api/v1/state/{stateName}/cities", "São Paulo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cityId").value(cityDTO.cityId().toString()))
                .andExpect(jsonPath("$[0].name").value(cityDTO.name()))
                .andExpect(jsonPath("$[0].stateName").value(cityDTO.stateName()));
    }

    @Test
    void shouldDeleteCityAndReturn204NoContent() throws Exception {
        doNothing().when(cityService).deleteCity(cityId);

        mockMvc.perform(delete("/api/v1/cities/{cityId}", cityId))
                .andExpect(status().isNoContent());
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateCityAndReturn400BadRequestWhenDataIsInvalid() throws Exception {
        CityCreateDTO invalidDTO = new CityCreateDTO("", "");

        mockMvc.perform(post("/api/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateCityAndReturn409ConflictWhenCityAlreadyExists() throws Exception {
        when(cityService.createCity(any(CityCreateDTO.class)))
                .thenThrow(new ObjectAlreadyExistsException("Cidade já cadastrada neste estado"));

        mockMvc.perform(post("/api/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityCreateDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotCreateCityAndReturn404NotFoundWhenStateDoesNotExist() throws Exception {
        when(cityService.createCity(any(CityCreateDTO.class)))
                .thenThrow(new ObjectNotFoundException("Estado não encontrado"));

        mockMvc.perform(post("/api/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityCreateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotGetCityAndReturn404NotFoundWhenCityDoesNotExist() throws Exception {
        when(cityService.getCity("CidadeInexistente", "EstadoInexistente"))
                .thenThrow(new ObjectNotFoundException("Cidade não encontrada"));

        mockMvc.perform(get("/api/v1/cities/{cityName}/{stateName}", "CidadeInexistente", "EstadoInexistente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotGetCitiesFromStateAndReturn404NotFoundWhenStateDoesNotExist() throws Exception {
        when(cityService.getCitiesFromState(eq("EstadoInexistente")))
                .thenThrow(new ObjectNotFoundException("Estado não encontrado"));

        mockMvc.perform(get("/api/v1/state/{stateName}/cities", "EstadoInexistente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteCityAndReturn404NotFoundWhenCityDoesNotExist() throws Exception {
        UUID randomId = UUID.randomUUID();
        doThrow(new ObjectNotFoundException("Cidade não encontrada"))
                .when(cityService).deleteCity(randomId);

        mockMvc.perform(delete("/api/v1/cities/{cityId}", randomId))
                .andExpect(status().isNotFound());
    }
}
