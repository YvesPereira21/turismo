package io.turismo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turismo.backend.dto.tag.TagCreateUpdateDTO;
import io.turismo.backend.dto.tag.TagDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.service.TagService;
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

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockitoBean
    private UserRepository userRepository;

    private UUID tagId;
    private TagDTO tagDTO;
    private TagCreateUpdateDTO tagCreateUpdateDTO;

    @BeforeEach
    void setup() {
        tagId = UUID.randomUUID();
        tagDTO = new TagDTO(tagId, "Ecoturismo");
        tagCreateUpdateDTO = new TagCreateUpdateDTO("Ecoturismo");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateTagAndReturn201Created() throws Exception {
        when(tagService.createTag(any(TagCreateUpdateDTO.class))).thenReturn(tagDTO);

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagCreateUpdateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(tagDTO.id().toString()))
                .andExpect(jsonPath("$.name").value(tagDTO.name()));
    }

    @Test
    void shouldGetTagByNameAndReturn200Ok() throws Exception {
        when(tagService.getTagByName("Ecoturismo")).thenReturn(tagDTO);

        mockMvc.perform(get("/api/v1/tags/{name}", "Ecoturismo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tagDTO.id().toString()))
                .andExpect(jsonPath("$.name").value(tagDTO.name()));
    }

    @Test
    void shouldGetAllTagsAndReturn200Ok() throws Exception {
        Page<TagDTO> page = new PageImpl<>(List.of(tagDTO));
        when(tagService.getAllTags(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(tagDTO.id().toString()))
                .andExpect(jsonPath("$.content[0].name").value(tagDTO.name()));
    }

    @Test
    void shouldUpdateTagAndReturn200Ok() throws Exception {
        when(tagService.updateTag(eq(tagId), any(TagCreateUpdateDTO.class))).thenReturn(tagDTO);

        mockMvc.perform(put("/api/v1/tags/{tagId}", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagCreateUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tagDTO.id().toString()))
                .andExpect(jsonPath("$.name").value(tagDTO.name()));
    }

    @Test
    void shouldDeleteTagByTechnologyNameAndReturn204NoContent() throws Exception {
        doNothing().when(tagService).deleteTagByTechnologyName("Ecoturismo");

        mockMvc.perform(delete("/api/v1/tags/{technologyName}", "Ecoturismo"))
                .andExpect(status().isNoContent());
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateTagAndReturn400BadRequestWhenDataIsInvalid() throws Exception {
        TagCreateUpdateDTO invalidDTO = new TagCreateUpdateDTO("");

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateTagAndReturn409ConflictWhenTagAlreadyExists() throws Exception {
        when(tagService.createTag(any(TagCreateUpdateDTO.class)))
                .thenThrow(new ObjectAlreadyExistsException("Tag já cadastrada"));

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagCreateUpdateDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotGetTagByNameAndReturn404NotFoundWhenTagDoesNotExist() throws Exception {
        when(tagService.getTagByName("Inexistente"))
                .thenThrow(new ObjectNotFoundException("Tag não encontrada"));

        mockMvc.perform(get("/api/v1/tags/{name}", "Inexistente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateTagAndReturn404NotFoundWhenTagDoesNotExist() throws Exception {
        when(tagService.updateTag(eq(tagId), any(TagCreateUpdateDTO.class)))
                .thenThrow(new ObjectNotFoundException("Tag não encontrada"));

        mockMvc.perform(put("/api/v1/tags/{tagId}", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagCreateUpdateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteTagByTechnologyNameAndReturn404NotFoundWhenTagDoesNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("Tag não encontrada"))
                .when(tagService).deleteTagByTechnologyName("Inexistente");

        mockMvc.perform(delete("/api/v1/tags/{technologyName}", "Inexistente"))
                .andExpect(status().isNotFound());
    }
}
