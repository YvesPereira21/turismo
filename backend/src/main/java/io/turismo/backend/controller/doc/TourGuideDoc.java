package io.turismo.backend.controller.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.tour_guide.TourGuideCreateDTO;
import io.turismo.backend.dto.tour_guide.TourGuideDTO;
import io.turismo.backend.dto.tour_guide.TourGuideUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Guia de Turismo", description = "Endpoints para gerenciamento de guias turísticos cadastrados")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface TourGuideDoc {

    @Operation(summary = "Criar guia de turismo", description = "Cadastra um novo guia de turismo no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Guia criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email ou Cadastur já cadastrado ou dados inválidos")
    })
    ResponseEntity<TourGuideDTO> createTourGuide(@Valid TourGuideCreateDTO dto);

    @Operation(summary = "Buscar guia de turismo", description = "Busca dados de um guia de turismo pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guia encontrado"),
            @ApiResponse(responseCode = "404", description = "Guia não encontrado")
    })
    ResponseEntity<TourGuideDTO> getTourGuide(UUID tourGuideId);

    @Operation(summary = "Atualizar guia de turismo", description = "Atualiza os dados de cadastro de um guia de turismo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guia atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Guia não encontrado")
    })
    ResponseEntity<TourGuideDTO> updateTourGuide(@Valid TourGuideUpdateDTO dto, UUID tourGuideId, UUID userId);

    @Operation(summary = "Deletar guia de turismo", description = "Exclui o cadastro de um guia de turismo do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Guia excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Guia não encontrado")
    })
    ResponseEntity<Void> deleteTourGuide(UUID tourGuideId, UUID userId);

    @Operation(summary = "Listar guias por ponto turístico", description = "Busca os guias de turismo vinculados a um ponto turístico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    ResponseEntity<Page<TourGuideDTO>> getTourGuidesByTouristSpot(UUID touristSpotId, Pageable pageable);
}
