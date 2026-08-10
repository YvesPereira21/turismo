package io.turismo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turismo.backend.dto.tour_guide.TourGuideCreateDTO;
import io.turismo.backend.dto.tour_guide.TourGuideDTO;
import io.turismo.backend.dto.tour_guide.TourGuideUpdateDTO;
import io.turismo.backend.dto.user.UserCreateDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.model.enums.TourGuideType;
import io.turismo.backend.service.TourGuideService;
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

@WebMvcTest(TourGuideController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TourGuideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TourGuideService tourGuideService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockitoBean
    private UserRepository userRepository;

    private UUID tourGuideId;
    private UUID userId;
    private UUID touristSpotId;
    private TourGuideDTO tourGuideDTO;
    private TourGuideCreateDTO tourGuideCreateDTO;
    private TourGuideUpdateDTO tourGuideUpdateDTO;

    @BeforeEach
    void setup() {
        tourGuideId = UUID.randomUUID();
        userId = UUID.randomUUID();
        touristSpotId = UUID.randomUUID();

        tourGuideDTO = new TourGuideDTO(tourGuideId, "123456789", TourGuideType.REGIONAL, userId, "Guia Teste", "11999999999");

        UserCreateDTO userCreateDTO = new UserCreateDTO("Guia Teste", "guia@email.com", "senha123", "11999999999");
        tourGuideCreateDTO = new TourGuideCreateDTO("123456789", TourGuideType.REGIONAL, userCreateDTO);

        tourGuideUpdateDTO = new TourGuideUpdateDTO("123456789", TourGuideType.REGIONAL, "Guia Atualizado", "11888888888");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateTourGuideAndReturn201Created() throws Exception {
        when(tourGuideService.createTourGuide(any(TourGuideCreateDTO.class))).thenReturn(tourGuideDTO);

        mockMvc.perform(post("/api/v1/tour-guides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tourGuideCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tourGuideId").value(tourGuideDTO.tourGuideId().toString()))
                .andExpect(jsonPath("$.cadastur").value(tourGuideDTO.cadastur()))
                .andExpect(jsonPath("$.type").value(tourGuideDTO.type().toString()))
                .andExpect(jsonPath("$.userId").value(tourGuideDTO.userId().toString()))
                .andExpect(jsonPath("$.name").value(tourGuideDTO.name()))
                .andExpect(jsonPath("$.phone").value(tourGuideDTO.phone()));
    }

    @Test
    void shouldGetTourGuideAndReturn200Ok() throws Exception {
        when(tourGuideService.getTourGuide(tourGuideId)).thenReturn(tourGuideDTO);

        mockMvc.perform(get("/api/v1/tour-guides/{tourGuideId}", tourGuideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tourGuideId").value(tourGuideDTO.tourGuideId().toString()))
                .andExpect(jsonPath("$.cadastur").value(tourGuideDTO.cadastur()))
                .andExpect(jsonPath("$.type").value(tourGuideDTO.type().toString()))
                .andExpect(jsonPath("$.userId").value(tourGuideDTO.userId().toString()))
                .andExpect(jsonPath("$.name").value(tourGuideDTO.name()))
                .andExpect(jsonPath("$.phone").value(tourGuideDTO.phone()));
    }

    @Test
    void shouldUpdateTourGuideAndReturn200Ok() throws Exception {
        when(tourGuideService.updateTourGuide(any(TourGuideUpdateDTO.class), eq(tourGuideId), any())).thenReturn(tourGuideDTO);

        mockMvc.perform(put("/api/v1/tour-guides/{tourGuideId}", tourGuideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tourGuideUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tourGuideId").value(tourGuideDTO.tourGuideId().toString()))
                .andExpect(jsonPath("$.cadastur").value(tourGuideDTO.cadastur()))
                .andExpect(jsonPath("$.type").value(tourGuideDTO.type().toString()))
                .andExpect(jsonPath("$.userId").value(tourGuideDTO.userId().toString()))
                .andExpect(jsonPath("$.name").value(tourGuideDTO.name()))
                .andExpect(jsonPath("$.phone").value(tourGuideDTO.phone()));
    }

    @Test
    void shouldDeleteTourGuideAndReturn204NoContent() throws Exception {
        doNothing().when(tourGuideService).deleteTourGuide(eq(tourGuideId), any());

        mockMvc.perform(delete("/api/v1/tour-guides/{tourGuideId}", tourGuideId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetTourGuidesByTouristSpotAndReturn200Ok() throws Exception {
        Page<TourGuideDTO> page = new PageImpl<>(List.of(tourGuideDTO));
        when(tourGuideService.getTourGuidesByTouristSpot(eq(touristSpotId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tourist-spots/{touristSpotId}/tour-guides", touristSpotId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].tourGuideId").value(tourGuideDTO.tourGuideId().toString()))
                .andExpect(jsonPath("$.content[0].cadastur").value(tourGuideDTO.cadastur()))
                .andExpect(jsonPath("$.content[0].type").value(tourGuideDTO.type().toString()))
                .andExpect(jsonPath("$.content[0].userId").value(tourGuideDTO.userId().toString()))
                .andExpect(jsonPath("$.content[0].name").value(tourGuideDTO.name()))
                .andExpect(jsonPath("$.content[0].phone").value(tourGuideDTO.phone()));
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateTourGuideAndReturn400BadRequestWhenDataIsInvalid() throws Exception {
        TourGuideCreateDTO invalidDTO = new TourGuideCreateDTO("", null, null);

        mockMvc.perform(post("/api/v1/tour-guides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateTourGuideAndReturn409ConflictWhenCadasturAlreadyExists() throws Exception {
        when(tourGuideService.createTourGuide(any(TourGuideCreateDTO.class)))
                .thenThrow(new ObjectAlreadyExistsException("Cadastur já cadastrado"));

        mockMvc.perform(post("/api/v1/tour-guides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tourGuideCreateDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotGetTourGuideAndReturn404NotFoundWhenTourGuideDoesNotExist() throws Exception {
        when(tourGuideService.getTourGuide(tourGuideId))
                .thenThrow(new ObjectNotFoundException("Guia não encontrado"));

        mockMvc.perform(get("/api/v1/tour-guides/{tourGuideId}", tourGuideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateTourGuideAndReturn404NotFoundWhenTourGuideDoesNotExist() throws Exception {
        when(tourGuideService.updateTourGuide(any(TourGuideUpdateDTO.class), eq(tourGuideId), any()))
                .thenThrow(new ObjectNotFoundException("Guia não encontrado"));

        mockMvc.perform(put("/api/v1/tour-guides/{tourGuideId}", tourGuideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tourGuideUpdateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateTourGuideAndReturn401UnauthorizedWhenUserIsNotOwner() throws Exception {
        when(tourGuideService.updateTourGuide(any(TourGuideUpdateDTO.class), eq(tourGuideId), any()))
                .thenThrow(new UserIsNotOwnerException("Você não tem autorização para alterar este perfil"));

        mockMvc.perform(put("/api/v1/tour-guides/{tourGuideId}", tourGuideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tourGuideUpdateDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotDeleteTourGuideAndReturn404NotFoundWhenTourGuideDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Guia não encontrado"))
                .when(tourGuideService).deleteTourGuide(eq(tourGuideId), any());

        mockMvc.perform(delete("/api/v1/tour-guides/{tourGuideId}", tourGuideId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteTourGuideAndReturn401UnauthorizedWhenUserIsNotAdminOrOwner() throws Exception {
        doThrow(new UserIsNotAdminOrOwnerException("Você não tem autorização para excluir este perfil"))
                .when(tourGuideService).deleteTourGuide(eq(tourGuideId), any());

        mockMvc.perform(delete("/api/v1/tour-guides/{tourGuideId}", tourGuideId))
                .andExpect(status().isUnauthorized());
    }
}
