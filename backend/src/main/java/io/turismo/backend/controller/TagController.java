package io.turismo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1")
@Validated
@Tag(name = "Tag", description = "Endpoints para gerenciamento de categorias/tags de pontos turísticos")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("/tags")
    @Operation(summary = "Criar tag", description = "Cadastra uma nova tag no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tag criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tag com esse nome já existe")
    })
    public ResponseEntity<TagDTO> createTag(@Valid @RequestBody TagCreateUpdateDTO dto) {
        TagDTO created = tagService.createTag(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/tags/{name}")
    @Operation(summary = "Buscar tag por nome", description = "Busca uma tag pelo seu nome (ignora maiúsculas/minúsculas)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag encontrada"),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada")
    })
    public ResponseEntity<TagDTO> getTagByName(@PathVariable String name) {
        TagDTO tag = tagService.getTagByName(name);
        return ResponseEntity.ok(tag);
    }

    @GetMapping("/tags")
    @Operation(summary = "Listar todas as tags", description = "Retorna uma página contendo todas as tags cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<TagDTO>> getAllTags(
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TagDTO> tags = tagService.getAllTags(pageable);
        return ResponseEntity.ok(tags);
    }

    @PutMapping("/tags/{tagId}")
    @Operation(summary = "Atualizar tag", description = "Atualiza os dados de uma tag existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada")
    })
    public ResponseEntity<TagDTO> updateTag(
            @PathVariable UUID tagId,
            @Valid @RequestBody TagCreateUpdateDTO dto
    ) {
        TagDTO updated = tagService.updateTag(tagId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/tags/{technologyName}")
    @Operation(summary = "Deletar tag por nome", description = "Exclui uma tag pelo nome correspondente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tag excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada")
    })
    public ResponseEntity<Void> deleteTagByTechnologyName(@PathVariable String technologyName) {
        tagService.deleteTagByTechnologyName(technologyName);
        return ResponseEntity.noContent().build();
    }
}
