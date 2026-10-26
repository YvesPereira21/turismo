package io.turismo.backend.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.spot_manager.SpotManagerCreateDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerSimpleDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Gerente de Ponto Turístico", description = "Endpoints para gerenciamento de administradores/gerentes de pontos turísticos")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface SpotManagerDocs {

    @Operation(summary = "Criar gerente", description = "Cadastra um novo gerente de ponto turístico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Gerente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já em uso")
    })
    ResponseEntity<SpotManagerSimpleDTO> createSpotManager(@Valid SpotManagerCreateDTO dto);

    @Operation(summary = "Buscar gerente (Simplificado)", description = "Busca dados simplificados de um gerente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gerente encontrado"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
    })
    ResponseEntity<SpotManagerSimpleDTO> getSpotManager(UUID spotManagerId);

    @Operation(summary = "Buscar perfil completo do gerente", description = "Retorna os detalhes completos do perfil do gerente logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do perfil completo retornados"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
    })
    ResponseEntity<SpotManagerDTO> currentSpotManager(UUID spotManagerId);

    @Operation(summary = "Atualizar gerente", description = "Atualiza informações cadastrais do gerente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gerente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
    })
    ResponseEntity<SpotManagerSimpleDTO> updateSpotManager(
            @Valid SpotManagerUpdateDTO spotManagerUpdateDTO,
            UUID spotManagerId,
            UUID userId
    );

    @Operation(summary = "Deletar gerente", description = "Remove o cadastro de um gerente do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Gerente removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
    })
    ResponseEntity<Void> deleteSpotManager(UUID spotManagerId, UUID userId);
}
