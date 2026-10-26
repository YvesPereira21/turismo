package io.turismo.backend.controller.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.tourist.TouristCreateDTO;
import io.turismo.backend.dto.tourist.TouristDTO;
import io.turismo.backend.dto.tourist.TouristUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Turista", description = "Endpoints para gerenciamento de turistas")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface TouristDoc {

    @Operation(summary = "Criar turista", description = "Cadastra um novo turista no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Turista criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email já cadastrado ou dados inválidos")
    })
    ResponseEntity<TouristDTO> createTourist(@Valid TouristCreateDTO dto);

    @Operation(summary = "Buscar turista", description = "Busca dados de um turista pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turista encontrado"),
            @ApiResponse(responseCode = "404", description = "Turista não encontrado")
    })
    ResponseEntity<TouristDTO> getTourist(UUID touristId);

    @Operation(summary = "Atualizar turista", description = "Atualiza os dados de cadastro de um turista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turista atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turista não encontrado")
    })
    ResponseEntity<TouristDTO> updateTourist(@Valid TouristUpdateDTO dto, UUID touristId, UUID userId);

    @Operation(summary = "Deletar turista", description = "Exclui o cadastro de um turista do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Turista excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turista não encontrado")
    })
    ResponseEntity<Void> deleteTourist(UUID touristId, UUID userId);
}
