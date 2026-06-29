package io.turismo.backend.service;

import io.turismo.backend.dto.tag.TagCreateUpdateDTO;
import io.turismo.backend.dto.tag.TagDTO;
import io.turismo.backend.mapper.TagMapper;
import io.turismo.backend.model.Tag;
import io.turismo.backend.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        boolean tagExists = tagRepository.existsByName(dto.name());
        if (tagExists) {
            throw new RuntimeException("Tag com esse nome já existe");
        }

        Tag newTag = new Tag();
        newTag.setName(dto.name());

        return tagMapper.toDTO(tagRepository.save(newTag));
    }

    public TagDTO getTagByName(String name) {
        Tag tag = tagRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Essa tag não existe"));

        return tagMapper.toDTO(tag);
    }

    public Page<TagDTO> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable).map(tagMapper::toDTO);
    }

    public TagDTO updateTag(UUID tagId, TagCreateUpdateDTO dto) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException(""));
        //verificar se o nome atualizado que o dto traz já existe
        if (tagRepository.existsByName(dto.name())) {
            throw new RuntimeException("Essa tag já existe.");
        }

        tag.setName(dto.name());

        return tagMapper.toDTO(tagRepository.save(tag));
    }

    public void deleteTagByTechnologyName(String technologyName) {
        Tag tag = tagRepository.findByName(technologyName)
                .orElseThrow(() -> new RuntimeException("Tag não encontrada."));

        tagRepository.delete(tag);
    }

    @Transactional(rollbackOn = Exception.class)
    public Set<Tag> convertNamesToTags(Set<String> names) {
        Set<Tag> tags = new HashSet<>();

        names.forEach(name -> {
            Tag tag = tagRepository.findByName(name)
                    .orElseThrow(() -> new RuntimeException("Tag " + name + " não encontrada"));
            tags.add(tag);
        }
        );

        return tags;
    }
}
