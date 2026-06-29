package io.turismo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.dto.warn.WarnCreateDTO;
import io.turismo.backend.dto.warn.WarnDTO;
import io.turismo.backend.service.WarnService;
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
@Tag(name = "Aviso", description = "Endpoints para gerenciamento de avisos de pontos turísticos")
public class WarnController {

    private final WarnService warnService;

    public WarnController(WarnService warnService) {
        this.warnService = warnService;
    }

    @PostMapping("/warns/spot/{touristSpotId}")
    @Operation(summary = "Criar aviso", description = "Cria um novo aviso para um ponto turístico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aviso criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    public ResponseEntity<WarnDTO> createWarn(
            @Valid @RequestBody WarnCreateDTO dto,
            @PathVariable UUID touristSpotId
    ) {
        WarnDTO created = warnService.createWarn(dto, touristSpotId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/warns/{warnId}")
    @Operation(summary = "Buscar aviso", description = "Busca detalhes de um aviso pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aviso encontrado"),
            @ApiResponse(responseCode = "404", description = "Aviso não encontrado")
    })
    public ResponseEntity<WarnDTO> getWarn(@PathVariable UUID warnId) {
        WarnDTO warn = warnService.getWarn(warnId);
        return ResponseEntity.ok(warn);
    }

    @GetMapping("/warns/spot/{touristSpotId}")
    @Operation(summary = "Listar avisos do ponto turístico", description = "Retorna uma página com todos os avisos de um ponto turístico específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<WarnDTO>> getAllTouristSpotWarn(
            @PathVariable UUID touristSpotId,
            @PageableDefault(page = 0, size = 10, sort = "eventDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<WarnDTO> warns = warnService.getAllTouristSpotWarn(touristSpotId, pageable);
        return ResponseEntity.ok(warns);
    }

    @DeleteMapping("/warns/{warnId}")
    @Operation(summary = "Deletar aviso", description = "Exclui um aviso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Aviso excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aviso não encontrado")
    })
    public ResponseEntity<Void> deleteWarn(@PathVariable UUID warnId) {
        warnService.deleteWarn(warnId);
        return ResponseEntity.noContent().build();
    }
}
