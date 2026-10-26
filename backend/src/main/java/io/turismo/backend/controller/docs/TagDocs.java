package io.turismo.backend.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.tag.TagCreateUpdateDTO;
import io.turismo.backend.dto.tag.TagDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Tag", description = "Endpoints para gerenciamento de categorias/tags de pontos turísticos")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface TagDocs {

    @Operation(summary = "Criar tag", description = "Cadastra uma nova tag no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tag criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tag com esse nome já existe")
    })
    ResponseEntity<TagDTO> createTag(@Valid TagCreateUpdateDTO dto);

    @Operation(summary = "Buscar tag por nome", description = "Busca uma tag pelo seu nome (ignora maiúsculas/minúsculas)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag encontrada"),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada")
    })
    ResponseEntity<TagDTO> getTagByName(String name);

    @Operation(summary = "Listar todas as tags", description = "Retorna uma página contendo todas as tags cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    ResponseEntity<Page<TagDTO>> getAllTags(Pageable pageable);

    @Operation(summary = "Atualizar tag", description = "Atualiza os dados de uma tag existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada")
    })
    ResponseEntity<TagDTO> updateTag(UUID tagId, @Valid TagCreateUpdateDTO dto);

    @Operation(summary = "Deletar tag por nome", description = "Exclui uma tag pelo nome correspondente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tag excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada")
    })
    ResponseEntity<Void> deleteTagByTechnologyName(String technologyName);
}
