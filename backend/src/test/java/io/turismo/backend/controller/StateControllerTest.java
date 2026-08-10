package io.turismo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turismo.backend.dto.state.StateCreateDTO;
import io.turismo.backend.dto.state.StateDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.service.StateService;
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

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StateController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private StateService stateService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockitoBean
    private UserRepository userRepository;

    private UUID stateId;
    private StateDTO stateDTO;
    private StateCreateDTO stateCreateDTO;

    @BeforeEach
    void setup() {
        stateId = UUID.randomUUID();
        stateDTO = new StateDTO(stateId, "São Paulo");
        stateCreateDTO = new StateCreateDTO("São Paulo");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateStateAndReturn201Created() throws Exception {
        doNothing().when(stateService).createState(any(StateCreateDTO.class));

        mockMvc.perform(post("/api/v1/states")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stateCreateDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetAllStatesAndReturn200Ok() throws Exception {
        when(stateService.getAllStates()).thenReturn(Set.of(stateDTO));

        mockMvc.perform(get("/api/v1/states")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stateId").value(stateDTO.stateId().toString()))
                .andExpect(jsonPath("$[0].name").value(stateDTO.name()));
    }

    @Test
    void shouldDeleteStateAndReturn204NoContent() throws Exception {
        doNothing().when(stateService).deleteState(stateId);

        mockMvc.perform(delete("/api/v1/states/{stateId}", stateId))
                .andExpect(status().isNoContent());
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateStateAndReturn400BadRequestWhenDataIsInvalid() throws Exception {
        StateCreateDTO invalidDTO = new StateCreateDTO("");

        mockMvc.perform(post("/api/v1/states")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateStateAndReturn409ConflictWhenStateAlreadyExists() throws Exception {
        doThrow(new ObjectAlreadyExistsException("Estado já cadastrado"))
                .when(stateService).createState(any(StateCreateDTO.class));

        mockMvc.perform(post("/api/v1/states")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stateCreateDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotDeleteStateAndReturn404NotFoundWhenStateDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Estado não encontrado"))
                .when(stateService).deleteState(stateId);

        mockMvc.perform(delete("/api/v1/states/{stateId}", stateId))
                .andExpect(status().isNotFound());
    }
}
