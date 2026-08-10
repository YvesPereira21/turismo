package io.turismo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.turismo.backend.dto.tourist.TouristCreateDTO;
import io.turismo.backend.dto.tourist.TouristDTO;
import io.turismo.backend.dto.tourist.TouristUpdateDTO;
import io.turismo.backend.dto.user.UserCreateDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.service.TouristService;
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

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TouristController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TouristControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private TouristService touristService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockitoBean
    private UserRepository userRepository;

    private UUID touristId;
    private UUID userId;
    private TouristDTO touristDTO;
    private TouristCreateDTO touristCreateDTO;
    private TouristUpdateDTO touristUpdateDTO;

    @BeforeEach
    void setup() {
        touristId = UUID.randomUUID();
        userId = UUID.randomUUID();

        touristDTO = new TouristDTO(touristId, LocalDate.of(1995, 5, 15), userId, "Turista Teste", "11999999999");

        UserCreateDTO userCreateDTO = new UserCreateDTO("Turista Teste", "turista@email.com", "senha123", "11999999999");
        touristCreateDTO = new TouristCreateDTO(LocalDate.of(1995, 5, 15), userCreateDTO);

        touristUpdateDTO = new TouristUpdateDTO(LocalDate.of(1995, 5, 15), "Turista Atualizado", "11888888888", Set.of("Português"));
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateTouristAndReturn201Created() throws Exception {
        when(touristService.createTourist(any(TouristCreateDTO.class))).thenReturn(touristDTO);

        mockMvc.perform(post("/api/v1/tourists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.touristId").value(touristDTO.touristId().toString()))
                .andExpect(jsonPath("$.birthDate").value(touristDTO.birthDate().toString()))
                .andExpect(jsonPath("$.userId").value(touristDTO.userId().toString()))
                .andExpect(jsonPath("$.name").value(touristDTO.name()))
                .andExpect(jsonPath("$.phone").value(touristDTO.phone()));
    }

    @Test
    void shouldGetTouristAndReturn200Ok() throws Exception {
        when(touristService.getTourist(touristId)).thenReturn(touristDTO);

        mockMvc.perform(get("/api/v1/tourists/{touristId}", touristId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.touristId").value(touristDTO.touristId().toString()))
                .andExpect(jsonPath("$.birthDate").value(touristDTO.birthDate().toString()))
                .andExpect(jsonPath("$.userId").value(touristDTO.userId().toString()))
                .andExpect(jsonPath("$.name").value(touristDTO.name()))
                .andExpect(jsonPath("$.phone").value(touristDTO.phone()));
    }

    @Test
    void shouldUpdateTouristAndReturn200Ok() throws Exception {
        when(touristService.updateTourist(any(TouristUpdateDTO.class), eq(touristId), any())).thenReturn(touristDTO);

        mockMvc.perform(put("/api/v1/tourists/{touristId}", touristId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.touristId").value(touristDTO.touristId().toString()))
                .andExpect(jsonPath("$.birthDate").value(touristDTO.birthDate().toString()))
                .andExpect(jsonPath("$.userId").value(touristDTO.userId().toString()))
                .andExpect(jsonPath("$.name").value(touristDTO.name()))
                .andExpect(jsonPath("$.phone").value(touristDTO.phone()));
    }

    @Test
    void shouldDeleteTouristAndReturn204NoContent() throws Exception {
        doNothing().when(touristService).deleteTourist(eq(touristId), any());

        mockMvc.perform(delete("/api/v1/tourists/{touristId}", touristId))
                .andExpect(status().isNoContent());
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateTouristAndReturn400BadRequestWhenDataIsInvalid() throws Exception {
        TouristCreateDTO invalidDTO = new TouristCreateDTO(null, null);

        mockMvc.perform(post("/api/v1/tourists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateTouristAndReturn409ConflictWhenEmailAlreadyExists() throws Exception {
        when(touristService.createTourist(any(TouristCreateDTO.class)))
                .thenThrow(new ObjectAlreadyExistsException("E-mail já cadastrado"));

        mockMvc.perform(post("/api/v1/tourists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristCreateDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotGetTouristAndReturn404NotFoundWhenTouristDoesNotExist() throws Exception {
        when(touristService.getTourist(touristId))
                .thenThrow(new ObjectNotFoundException("Turista não encontrado"));

        mockMvc.perform(get("/api/v1/tourists/{touristId}", touristId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateTouristAndReturn404NotFoundWhenTouristDoesNotExist() throws Exception {
        when(touristService.updateTourist(any(TouristUpdateDTO.class), eq(touristId), any()))
                .thenThrow(new ObjectNotFoundException("Turista não encontrado"));

        mockMvc.perform(put("/api/v1/tourists/{touristId}", touristId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristUpdateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateTouristAndReturn401UnauthorizedWhenUserIsNotOwner() throws Exception {
        when(touristService.updateTourist(any(TouristUpdateDTO.class), eq(touristId), any()))
                .thenThrow(new UserIsNotOwnerException("Você não tem autorização para alterar este perfil"));

        mockMvc.perform(put("/api/v1/tourists/{touristId}", touristId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(touristUpdateDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotDeleteTouristAndReturn404NotFoundWhenTouristDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Turista não encontrado"))
                .when(touristService).deleteTourist(eq(touristId), any());

        mockMvc.perform(delete("/api/v1/tourists/{touristId}", touristId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteTouristAndReturn401UnauthorizedWhenUserIsNotAdminOrOwner() throws Exception {
        doThrow(new UserIsNotAdminOrOwnerException("Você não tem autorização para excluir este perfil"))
                .when(touristService).deleteTourist(eq(touristId), any());

        mockMvc.perform(delete("/api/v1/tourists/{touristId}", touristId))
                .andExpect(status().isUnauthorized());
    }
}
