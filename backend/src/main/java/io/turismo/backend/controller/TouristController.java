package io.turismo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.dto.tourist.TouristCreateDTO;
import io.turismo.backend.dto.tourist.TouristDTO;
import io.turismo.backend.dto.tourist.TouristUpdateDTO;
import io.turismo.backend.service.TouristService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.turismo.backend.config.SecurityConfig;

@RestController
@SecurityRequirement(name = SecurityConfig.SECURITY)
@RequestMapping(path = "/api/v1")
@Validated
@Tag(name = "Turista", description = "Endpoints para gerenciamento de turistas")
public class TouristController {

    private final TouristService touristService;

    public TouristController(TouristService touristService) {
        this.touristService = touristService;
    }

    @PostMapping("/tourists")
    @Operation(summary = "Criar turista", description = "Cadastra um novo turista no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Turista criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email já cadastrado ou dados inválidos")
    })
    public ResponseEntity<TouristDTO> createTourist(@Valid @RequestBody TouristCreateDTO dto) {
        TouristDTO created = touristService.createTourist(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TOURIST')")
    @GetMapping("/tourists/{touristId}")
    @Operation(summary = "Buscar turista", description = "Busca dados de um turista pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turista encontrado"),
            @ApiResponse(responseCode = "404", description = "Turista não encontrado")
    })
    public ResponseEntity<TouristDTO> getTourist(@PathVariable UUID touristId) {
        TouristDTO tourist = touristService.getTourist(touristId);
        return ResponseEntity.ok(tourist);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TOURIST')")
    @PutMapping("/tourists/{touristId}")
    @Operation(summary = "Atualizar turista", description = "Atualiza os dados de cadastro de um turista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turista atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turista não encontrado")
    })
    public ResponseEntity<TouristDTO> updateTourist(
            @Valid @RequestBody TouristUpdateDTO dto,
            @PathVariable UUID touristId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        TouristDTO updated = touristService.updateTourist(dto, touristId, userId);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TOURIST')")
    @DeleteMapping("/tourists/{touristId}")
    @Operation(summary = "Deletar turista", description = "Exclui o cadastro de um turista do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Turista excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turista não encontrado")
    })
    public ResponseEntity<Void> deleteTourist(
            @PathVariable UUID touristId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        touristService.deleteTourist(touristId, userId);
        return ResponseEntity.noContent().build();
    }
}
