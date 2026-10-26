package io.turismo.backend.controller.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.geojson.GeoFeatureCollectionDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotCreateDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotListDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotToMapDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

@Tag(name = "Ponto Turístico", description = "Endpoints para gerenciamento de pontos turísticos e buscas por geolocalização")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface TouristSpotDoc {

    @Operation(summary = "Criar ponto turístico", description = "Cria um novo ponto turístico vinculado a um gerente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ponto turístico criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Gerente ou cidade não encontrada")
    })
    ResponseEntity<TouristSpotDTO> createTouristSpot(@Valid TouristSpotCreateDTO dto, UUID userId);

    @Operation(summary = "Buscar ponto turístico por ID", description = "Retorna os detalhes de um ponto turístico específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ponto turístico encontrado"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    ResponseEntity<TouristSpotDTO> getTouristSpot(UUID touristSpotId);

    @Operation(summary = "Buscar todos pontos turísticos para implementar no mapa", description = "Retorna os pontos turísticos para o mapa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ponto turístico encontrado"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    ResponseEntity<GeoFeatureCollectionDTO<TouristSpotToMapDTO>> getTouristSpotsToMap();

    @Operation(summary = "Listar todos os pontos turísticos", description = "Retorna uma página contendo todos os pontos turísticos cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    ResponseEntity<Page<TouristSpotListDTO>> getTouristSpots(
            String name,
            String cityName,
            String stateName,
            Set<String> tags,
            Double longitude,
            Double latitude,
            Double radius,
            Pageable pageable
    );

    @Operation(summary = "Listar pontos turísticos do gerente", description = "Retorna os pontos turísticos administrados por um gerente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    ResponseEntity<Page<TouristSpotListDTO>> getSpotManagerTouristSpots(UUID spotManagerId, Pageable pageable);

    @Operation(summary = "Atualizar ponto turístico", description = "Atualiza as informações de um ponto turístico específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ponto turístico atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico, cidade ou gerente não encontrado")
    })
    ResponseEntity<Void> updateTouristSpot(UUID touristSpotId, @Valid TouristSpotUpdateDTO dto, UUID userId);

    @Operation(summary = "Deletar ponto turístico", description = "Remove um ponto turístico do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ponto turístico removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    ResponseEntity<Void> deleteTouristSpot(UUID touristSpotId, UUID userId);
}
