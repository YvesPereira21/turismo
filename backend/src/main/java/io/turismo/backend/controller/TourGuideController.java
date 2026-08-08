package io.turismo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.dto.tour_guide.TourGuideCreateDTO;
import io.turismo.backend.dto.tour_guide.TourGuideDTO;
import io.turismo.backend.dto.tour_guide.TourGuideUpdateDTO;
import io.turismo.backend.service.TourGuideService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@Tag(name = "Guia de Turismo", description = "Endpoints para gerenciamento de guias turísticos cadastrados")
public class TourGuideController {

    private final TourGuideService tourGuideService;

    public TourGuideController(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    @PostMapping("/tour-guides")
    @Operation(summary = "Criar guia de turismo", description = "Cadastra um novo guia de turismo no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Guia criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email ou Cadastur já cadastrado ou dados inválidos")
    })
    public ResponseEntity<TourGuideDTO> createTourGuide(@Valid @RequestBody TourGuideCreateDTO dto) {
        TourGuideDTO created = tourGuideService.createTourGuide(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/tour-guides/{tourGuideId}")
    @Operation(summary = "Buscar guia de turismo", description = "Busca dados de um guia de turismo pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guia encontrado"),
            @ApiResponse(responseCode = "404", description = "Guia não encontrado")
    })
    public ResponseEntity<TourGuideDTO> getTourGuide(@PathVariable UUID tourGuideId) {
        TourGuideDTO guide = tourGuideService.getTourGuide(tourGuideId);
        return ResponseEntity.ok(guide);
    }

    @PreAuthorize("hasRole('TOURGUIDE')")
    @PutMapping("/tour-guides/{tourGuideId}")
    @Operation(summary = "Atualizar guia de turismo", description = "Atualiza os dados de cadastro de um guia de turismo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guia atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Guia não encontrado")
    })
    public ResponseEntity<TourGuideDTO> updateTourGuide(
            @Valid @RequestBody TourGuideUpdateDTO dto,
            @PathVariable UUID tourGuideId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        TourGuideDTO updated = tourGuideService.updateTourGuide(dto, tourGuideId, userId);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TOURGUIDE')")
    @DeleteMapping("/tour-guides/{tourGuideId}")
    @Operation(summary = "Deletar guia de turismo", description = "Exclui o cadastro de um guia de turismo do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Guia excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Guia não encontrado")
    })
    public ResponseEntity<Void> deleteTourGuide(
            @PathVariable UUID tourGuideId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        tourGuideService.deleteTourGuide(tourGuideId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tourist-spots/{touristSpotId}/tour-guides")
    @Operation(summary = "Listar guias por ponto turístico", description = "Busca os guias de turismo vinculados a um ponto turístico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<TourGuideDTO>> getTourGuidesByTouristSpot(
            @PathVariable UUID touristSpotId,
            @PageableDefault(page = 0, size = 10, sort = "user.name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<TourGuideDTO> guides = tourGuideService.getTourGuidesByTouristSpot(touristSpotId, pageable);
        return ResponseEntity.ok(guides);
    }
}
