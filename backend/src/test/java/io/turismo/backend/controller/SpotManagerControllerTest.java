package io.turismo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turismo.backend.dto.spot_manager.SpotManagerCreateDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerSimpleDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerUpdateDTO;
import io.turismo.backend.dto.user.UserCreateDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.model.enums.ManagerType;
import io.turismo.backend.service.SpotManagerService;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpotManagerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SpotManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private SpotManagerService spotManagerService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockitoBean
    private UserRepository userRepository;

    private UUID spotManagerId;
    private UUID userId;
    private SpotManagerSimpleDTO simpleDTO;
    private SpotManagerDTO fullDTO;
    private SpotManagerCreateDTO createDTO;
    private SpotManagerUpdateDTO updateDTO;

    @BeforeEach
    void setup() {
        spotManagerId = UUID.randomUUID();
        userId = UUID.randomUUID();

        simpleDTO = new SpotManagerSimpleDTO(spotManagerId, ManagerType.PUBLIC, "Gerente Teste", "11999999999");
        fullDTO = new SpotManagerDTO(spotManagerId, userId, ManagerType.PUBLIC, "Gerente Teste", "11999999999");

        UserCreateDTO userCreateDTO = new UserCreateDTO("Gerente Teste", "gerente@email.com", "senha123", "11999999999");
        createDTO = new SpotManagerCreateDTO(ManagerType.PUBLIC, userCreateDTO);

        updateDTO = new SpotManagerUpdateDTO("Gerente Atualizado", "11888888888", ManagerType.PRIVATE);
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateSpotManagerAndReturn201Created() throws Exception {
        when(spotManagerService.createSpotManager(any(SpotManagerCreateDTO.class))).thenReturn(simpleDTO);

        mockMvc.perform(post("/api/v1/spot-managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.spotManagerId").value(simpleDTO.spotManagerId().toString()))
                .andExpect(jsonPath("$.managerType").value(simpleDTO.managerType().toString()))
                .andExpect(jsonPath("$.name").value(simpleDTO.name()))
                .andExpect(jsonPath("$.phone").value(simpleDTO.phone()));
    }

    @Test
    void shouldGetSpotManagerAndReturn200Ok() throws Exception {
        when(spotManagerService.getSpotManager(spotManagerId)).thenReturn(simpleDTO);

        mockMvc.perform(get("/api/v1/spot-managers/{spotManagerId}", spotManagerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spotManagerId").value(simpleDTO.spotManagerId().toString()))
                .andExpect(jsonPath("$.managerType").value(simpleDTO.managerType().toString()))
                .andExpect(jsonPath("$.name").value(simpleDTO.name()))
                .andExpect(jsonPath("$.phone").value(simpleDTO.phone()));
    }

    @Test
    void shouldGetCurrentSpotManagerAndReturn200Ok() throws Exception {
        when(spotManagerService.currentSpotManager(spotManagerId)).thenReturn(fullDTO);

        mockMvc.perform(get("/api/v1/spot-managers/{spotManagerId}/current", spotManagerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spotManagerId").value(fullDTO.spotManagerId().toString()))
                .andExpect(jsonPath("$.userId").value(fullDTO.userId().toString()))
                .andExpect(jsonPath("$.managerType").value(fullDTO.managerType().toString()))
                .andExpect(jsonPath("$.name").value(fullDTO.name()))
                .andExpect(jsonPath("$.phone").value(fullDTO.phone()));
    }

    @Test
    void shouldUpdateSpotManagerAndReturn200Ok() throws Exception {
        when(spotManagerService.updateSpotManager(any(SpotManagerUpdateDTO.class), eq(spotManagerId), any())).thenReturn(simpleDTO);

        mockMvc.perform(put("/api/v1/spot-managers/{spotManagerId}", spotManagerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spotManagerId").value(simpleDTO.spotManagerId().toString()))
                .andExpect(jsonPath("$.managerType").value(simpleDTO.managerType().toString()))
                .andExpect(jsonPath("$.name").value(simpleDTO.name()))
                .andExpect(jsonPath("$.phone").value(simpleDTO.phone()));
    }

    @Test
    void shouldDeleteSpotManagerAndReturn204NoContent() throws Exception {
        doNothing().when(spotManagerService).deleteSpotManager(eq(spotManagerId), any());

        mockMvc.perform(delete("/api/v1/spot-managers/{spotManagerId}", spotManagerId))
                .andExpect(status().isNoContent());
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateSpotManagerAndReturn400BadRequestWhenDataIsInvalid() throws Exception {
        SpotManagerCreateDTO invalidDTO = new SpotManagerCreateDTO(null, null);

        mockMvc.perform(post("/api/v1/spot-managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateSpotManagerAndReturn409ConflictWhenEmailAlreadyExists() throws Exception {
        when(spotManagerService.createSpotManager(any(SpotManagerCreateDTO.class)))
                .thenThrow(new ObjectAlreadyExistsException("E-mail já está em uso"));

        mockMvc.perform(post("/api/v1/spot-managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotGetSpotManagerAndReturn404NotFoundWhenSpotManagerDoesNotExist() throws Exception {
        when(spotManagerService.getSpotManager(spotManagerId))
                .thenThrow(new ObjectNotFoundException("Gerente não encontrado"));

        mockMvc.perform(get("/api/v1/spot-managers/{spotManagerId}", spotManagerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotGetCurrentSpotManagerAndReturn404NotFoundWhenSpotManagerDoesNotExist() throws Exception {
        when(spotManagerService.currentSpotManager(spotManagerId))
                .thenThrow(new ObjectNotFoundException("Gerente não encontrado"));

        mockMvc.perform(get("/api/v1/spot-managers/{spotManagerId}/current", spotManagerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateSpotManagerAndReturn404NotFoundWhenSpotManagerDoesNotExist() throws Exception {
        when(spotManagerService.updateSpotManager(any(SpotManagerUpdateDTO.class), eq(spotManagerId), any()))
                .thenThrow(new ObjectNotFoundException("Gerente não encontrado"));

        mockMvc.perform(put("/api/v1/spot-managers/{spotManagerId}", spotManagerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateSpotManagerAndReturn401UnauthorizedWhenUserIsNotOwner() throws Exception {
        when(spotManagerService.updateSpotManager(any(SpotManagerUpdateDTO.class), eq(spotManagerId), any()))
                .thenThrow(new UserIsNotOwnerException("Você não tem autorização para alterar este perfil"));

        mockMvc.perform(put("/api/v1/spot-managers/{spotManagerId}", spotManagerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotDeleteSpotManagerAndReturn404NotFoundWhenSpotManagerDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Gerente não encontrado"))
                .when(spotManagerService).deleteSpotManager(eq(spotManagerId), any());

        mockMvc.perform(delete("/api/v1/spot-managers/{spotManagerId}", spotManagerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteSpotManagerAndReturn401UnauthorizedWhenUserIsNotAdminOrOwner() throws Exception {
        doThrow(new UserIsNotAdminOrOwnerException("Você não tem autorização para excluir este perfil"))
                .when(spotManagerService).deleteSpotManager(eq(spotManagerId), any());

        mockMvc.perform(delete("/api/v1/spot-managers/{spotManagerId}", spotManagerId))
                .andExpect(status().isUnauthorized());
    }
}
