package io.turismo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.dto.spot_manager.SpotManagerCreateDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerSimpleDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerUpdateDTO;
import io.turismo.backend.service.SpotManagerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1")
@Validated
@Tag(name = "Gerente de Ponto Turístico", description = "Endpoints para gerenciamento de administradores/gerentes de pontos turísticos")
public class SpotManagerController {

    private final SpotManagerService spotManagerService;

    public SpotManagerController(SpotManagerService spotManagerService) {
        this.spotManagerService = spotManagerService;
    }

    @PostMapping("/spot-managers")
    @Operation(summary = "Criar gerente", description = "Cadastra um novo gerente de ponto turístico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Gerente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já em uso")
    })
    public ResponseEntity<SpotManagerSimpleDTO> createSpotManager(@Valid @RequestBody SpotManagerCreateDTO dto) {
        SpotManagerSimpleDTO created = spotManagerService.createSpotManager(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/spot-managers/{spotManagerId}")
    @Operation(summary = "Buscar gerente (Simplificado)", description = "Busca dados simplificados de um gerente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gerente encontrado"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
    })
    public ResponseEntity<SpotManagerSimpleDTO> getSpotManager(@PathVariable UUID spotManagerId) {
        SpotManagerSimpleDTO manager = spotManagerService.getSpotManager(spotManagerId);
        return ResponseEntity.ok(manager);
    }

    @GetMapping("/spot-managers/{spotManagerId}/current")
    @Operation(summary = "Buscar perfil completo do gerente", description = "Retorna os detalhes completos do perfil do gerente logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do perfil completo retornados"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
    })
    public ResponseEntity<SpotManagerDTO> currentSpotManager(@PathVariable UUID spotManagerId) {
        SpotManagerDTO manager = spotManagerService.currentSpotManager(spotManagerId);
        return ResponseEntity.ok(manager);
    }

    @PutMapping("/spot-managers/{spotManagerId}")
    @Operation(summary = "Atualizar gerente", description = "Atualiza informações cadastrais do gerente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gerente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
    })
    public ResponseEntity<SpotManagerSimpleDTO> updateSpotManager(
            @Valid @RequestBody SpotManagerUpdateDTO spotManagerUpdateDTO,
            @PathVariable UUID spotManagerId
    ) {
        SpotManagerSimpleDTO updated = spotManagerService.updateSpotManager(spotManagerUpdateDTO, spotManagerId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/spot-managers/{spotManagerId}")
    @Operation(summary = "Deletar gerente", description = "Remove o cadastro de um gerente do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Gerente removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
    })
    public ResponseEntity<Void> deleteSpotManager(@PathVariable UUID spotManagerId) {
        spotManagerService.deleteSpotManager(spotManagerId);
        return ResponseEntity.noContent().build();
    }
}
