package io.turismo.backend.service;

import io.turismo.backend.dto.tag.TagCreateUpdateDTO;
import io.turismo.backend.dto.tag.TagDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.mapper.TagMapper;
import io.turismo.backend.model.Tag;
import io.turismo.backend.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;
    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagService tagService;

    private Tag tag;
    private TagDTO tagDTO;
    private TagCreateUpdateDTO tagCreateUpdateDTO;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        ReflectionTestUtils.setField(tag, "id", UUID.randomUUID());
        tag.setName("Praia");

        tagDTO = new TagDTO(tag.getId(), tag.getName());
        tagCreateUpdateDTO = new TagCreateUpdateDTO("Praia");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateTag() {
        when(tagRepository.existsByName(tagCreateUpdateDTO.name())).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.toDTO(tag)).thenReturn(tagDTO);

        TagDTO result = tagService.createTag(tagCreateUpdateDTO);

        assertNotNull(result);
        assertEquals("Praia", result.name());

        verify(tagRepository, times(1)).existsByName(tagCreateUpdateDTO.name());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void shouldGetTagByName() {
        when(tagRepository.findByNameContainingIgnoreCase("Praia")).thenReturn(Optional.of(tag));
        when(tagMapper.toDTO(tag)).thenReturn(tagDTO);

        TagDTO result = tagService.getTagByName("Praia");

        assertNotNull(result);
        assertEquals("Praia", result.name());

        verify(tagRepository, times(1)).findByNameContainingIgnoreCase("Praia");
    }

    @Test
    void shouldGetAllTags() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> tagPage = new PageImpl<>(List.of(tag));

        when(tagRepository.findAll(pageable)).thenReturn(tagPage);
        when(tagMapper.toDTO(tag)).thenReturn(tagDTO);

        Page<TagDTO> result = tagService.getAllTags(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(tagRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldUpdateTag() {
        UUID tagId = tag.getId();
        TagCreateUpdateDTO updateDTO = new TagCreateUpdateDTO("Montanha");

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(tagRepository.existsByName("Montanha")).thenReturn(false);
        when(tagRepository.save(tag)).thenReturn(tag);
        when(tagMapper.toDTO(tag)).thenReturn(new TagDTO(tagId, "Montanha"));

        TagDTO result = tagService.updateTag(tagId, updateDTO);

        assertNotNull(result);
        assertEquals("Montanha", result.name());

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagRepository, times(1)).save(tag);
    }

    @Test
    void shouldDeleteTagByTechnologyName() {
        when(tagRepository.findByName("Praia")).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).delete(tag);

        assertDoesNotThrow(() -> tagService.deleteTagByTechnologyName("Praia"));

        verify(tagRepository, times(1)).findByName("Praia");
        verify(tagRepository, times(1)).delete(tag);
    }

    @Test
    void shouldConvertNamesToTags() {
        Set<String> names = Set.of("Praia");
        when(tagRepository.findByName("Praia")).thenReturn(Optional.of(tag));

        Set<Tag> result = tagService.convertNamesToTags(names);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(tag));

        verify(tagRepository, times(1)).findByName("Praia");
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateTagAndThrowExceptionWhenTagAlreadyExists() {
        when(tagRepository.existsByName(tagCreateUpdateDTO.name())).thenReturn(true);

        assertThrows(
                ObjectAlreadyExistsException.class,
                () -> tagService.createTag(tagCreateUpdateDTO)
        );

        verify(tagRepository, times(1)).existsByName(tagCreateUpdateDTO.name());
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void shouldNotGetTagByNameAndThrowExceptionWhenNotFound() {
        when(tagRepository.findByNameContainingIgnoreCase("Inexistente")).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> tagService.getTagByName("Inexistente")
        );

        verify(tagRepository, times(1)).findByNameContainingIgnoreCase("Inexistente");
    }

    @Test
    void shouldNotUpdateTagAndThrowExceptionWhenTagNotFound() {
        UUID tagId = UUID.randomUUID();
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> tagService.updateTag(tagId, tagCreateUpdateDTO)
        );

        verify(tagRepository, times(1)).findById(tagId);
    }

    @Test
    void shouldNotUpdateTagAndThrowExceptionWhenNameAlreadyExists() {
        UUID tagId = tag.getId();
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(tagRepository.existsByName(tagCreateUpdateDTO.name())).thenReturn(true);

        assertThrows(
                ObjectAlreadyExistsException.class,
                () -> tagService.updateTag(tagId, tagCreateUpdateDTO)
        );

        verify(tagRepository, times(1)).existsByName(tagCreateUpdateDTO.name());
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void shouldNotDeleteTagByTechnologyNameAndThrowExceptionWhenNotFound() {
        when(tagRepository.findByName("Inexistente")).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> tagService.deleteTagByTechnologyName("Inexistente")
        );

        verify(tagRepository, times(1)).findByName("Inexistente");
        verify(tagRepository, never()).delete(any(Tag.class));
    }

    @Test
    void shouldNotConvertNamesToTagsAndThrowExceptionWhenTagNotFound() {
        Set<String> names = Set.of("Inexistente");
        when(tagRepository.findByName("Inexistente")).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> tagService.convertNamesToTags(names)
        );

        verify(tagRepository, times(1)).findByName("Inexistente");
    }
}
