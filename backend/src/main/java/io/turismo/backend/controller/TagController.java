package io.turismo.backend.controller;

import io.turismo.backend.controller.doc.TagDoc;
import io.turismo.backend.dto.tag.TagCreateUpdateDTO;
import io.turismo.backend.dto.tag.TagDTO;
import io.turismo.backend.service.TagService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class TagController implements TagDoc {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/tags")
    public ResponseEntity<TagDTO> createTag(@RequestBody TagCreateUpdateDTO dto) {
        TagDTO created = tagService.createTag(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @GetMapping("/tags/{name}")
    public ResponseEntity<TagDTO> getTagByName(@PathVariable String name) {
        TagDTO tag = tagService.getTagByName(name);
        return ResponseEntity.ok(tag);
    }

    @Override
    @GetMapping("/tags")
    public ResponseEntity<Page<TagDTO>> getAllTags(
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TagDTO> tags = tagService.getAllTags(pageable);
        return ResponseEntity.ok(tags);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/tags/{tagId}")
    public ResponseEntity<TagDTO> updateTag(
            @PathVariable UUID tagId,
            @RequestBody TagCreateUpdateDTO dto
    ) {
        TagDTO updated = tagService.updateTag(tagId, dto);
        return ResponseEntity.ok(updated);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/tags/{technologyName}")
    public ResponseEntity<Void> deleteTagByTechnologyName(@PathVariable String technologyName) {
        tagService.deleteTagByTechnologyName(technologyName);
        return ResponseEntity.noContent().build();
    }
}
