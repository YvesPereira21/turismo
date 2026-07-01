package io.turismo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.dto.geojson.GeoFeatureCollectionDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotCreateDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotListDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotToMapDTO;
import io.turismo.backend.model.enums.StateName;
import io.turismo.backend.service.TouristSpotService;
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
@Tag(name = "Ponto Turístico", description = "Endpoints para gerenciamento de pontos turísticos e buscas por geolocalização")
public class TouristSpotController {

    private final TouristSpotService touristSpotService;

    public TouristSpotController(TouristSpotService touristSpotService) {
        this.touristSpotService = touristSpotService;
    }

    @PostMapping("/manager/{spotManagerId}/tourist-spots")
    @Operation(summary = "Criar ponto turístico", description = "Cria um novo ponto turístico vinculado a um gerente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ponto turístico criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Gerente ou cidade não encontrada")
    })
    public ResponseEntity<TouristSpotDTO> createTouristSpot(
            @Valid @RequestBody TouristSpotCreateDTO dto,
            @PathVariable UUID spotManagerId
    ) {
        TouristSpotDTO created = touristSpotService.createTouristSpot(dto, spotManagerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/tourist-spots/{touristSpotId}")
    @Operation(summary = "Buscar ponto turístico por ID", description = "Retorna os detalhes de um ponto turístico específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ponto turístico encontrado"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    public ResponseEntity<TouristSpotDTO> getTouristSpot(@PathVariable UUID touristSpotId) {
        TouristSpotDTO spot = touristSpotService.getTouristSpot(touristSpotId);
        return ResponseEntity.ok(spot);
    }

    @GetMapping("/spots-to-map/")
    @Operation(summary = "Buscar todos pontos turísticos para implementar no mapa", description = "Retorna os pontos turísticos para o mapa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ponto turístico encontrado"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    public ResponseEntity<GeoFeatureCollectionDTO<TouristSpotToMapDTO>> getTouristSpotsToMap() {
        return ResponseEntity.ok(touristSpotService.getTouristSpotsToMap());
    }

    @GetMapping("/tourist-spots")
    @Operation(summary = "Listar todos os pontos turísticos", description = "Retorna uma página contendo todos os pontos turísticos cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<TouristSpotListDTO>> getTouristSpots(
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TouristSpotListDTO> spots = touristSpotService.getTouristSpots(pageable);
        return ResponseEntity.ok(spots);
    }

    @GetMapping("/state/{stateName}/tourist-spots")
    @Operation(summary = "Listar pontos turísticos de um estado", description = "Retorna os pontos turísticos localizados em um determinado estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<TouristSpotListDTO>> getTouristSpotsFromState(
            @PathVariable StateName stateName,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TouristSpotListDTO> spots = touristSpotService.getTouristSpotsFromState(stateName, pageable);
        return ResponseEntity.ok(spots);
    }

    @GetMapping("/manager/{spotManagerId}/all-tourist-spots")
    @Operation(summary = "Listar pontos turísticos do gerente", description = "Retorna os pontos turísticos administrados por um gerente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<TouristSpotListDTO>> getSpotManagerTouristSpots(
            @PathVariable UUID spotManagerId,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TouristSpotListDTO> spots = touristSpotService.getSpotManagerTouristSpots(spotManagerId, pageable);
        return ResponseEntity.ok(spots);
    }

    @GetMapping("/tourist-spots/near")
    @Operation(summary = "Buscar pontos turísticos próximos", description = "Busca pontos turísticos em um determinado raio de distância (em metros) de uma coordenada geográfica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pontos turísticos próximos retornados com sucesso")
    })
    public ResponseEntity<Page<TouristSpotListDTO>> getNearTouristSpots(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam Double radius,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TouristSpotListDTO> spots = touristSpotService.getNearTouristSpots(longitude, latitude, radius, pageable);
        return ResponseEntity.ok(spots);
    }
}
