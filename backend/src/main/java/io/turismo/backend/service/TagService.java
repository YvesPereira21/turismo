package io.turismo.backend.service;

import io.turismo.backend.dto.tag.TagCreateUpdateDTO;
import io.turismo.backend.dto.tag.TagDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.mapper.TagMapper;
import io.turismo.backend.model.Tag;
import io.turismo.backend.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagService(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Transactional
    public TagDTO createTag(TagCreateUpdateDTO dto) {
        log.info("Creating tag with name: {}", dto.name());
        boolean tagExists = tagRepository.existsByName(dto.name());
        if (tagExists) {
            throw new ObjectAlreadyExistsException("Tag com esse nome já existe");
        }

        Tag newTag = new Tag();
        newTag.setName(dto.name());

        Tag saved = tagRepository.save(newTag);
        log.info("Tag created with ID: {}", saved.getId());
        return tagMapper.toDTO(saved);
    }

    public TagDTO getTagByName(String name) {
        log.info("Fetching tag by name: {}", name);
        Tag tag = tagRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ObjectNotFoundException("Essa tag não existe"));

        return tagMapper.toDTO(tag);
    }

    public Page<TagDTO> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable).map(tagMapper::toDTO);
    }

    public TagDTO updateTag(UUID tagId, TagCreateUpdateDTO dto) {
        log.info("Updating tag ID: {}", tagId);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException(""));
        //verificar se o nome atualizado que o dto traz já existe
        if (tagRepository.existsByName(dto.name())) {
            throw new ObjectAlreadyExistsException("Essa tag já existe.");
        }

        tag.setName(dto.name());

        return tagMapper.toDTO(tagRepository.save(tag));
    }

    public void deleteTagByTechnologyName(String technologyName) {
        log.info("Deleting tag by name: {}", technologyName);
        Tag tag = tagRepository.findByName(technologyName)
                .orElseThrow(() -> new ObjectNotFoundException("Tag não encontrada."));

        tagRepository.delete(tag);
    }

    @Transactional(rollbackOn = Exception.class)
    public Set<Tag> convertNamesToTags(Set<String> names) {
        Set<Tag> tags = new HashSet<>();

        names.forEach(name -> {
            Tag tag = tagRepository.findByName(name)
                    .orElseThrow(() -> new ObjectNotFoundException("Tag " + name + " não encontrada"));
            tags.add(tag);
        }
        );

        return tags;
    }
}
