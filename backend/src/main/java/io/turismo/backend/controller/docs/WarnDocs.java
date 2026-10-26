package io.turismo.backend.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.warn.WarnCreateDTO;
import io.turismo.backend.dto.warn.WarnDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Aviso", description = "Endpoints para gerenciamento de avisos de pontos turísticos")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface WarnDocs {

    @Operation(summary = "Criar aviso", description = "Cria um novo aviso para um ponto turístico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aviso criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    ResponseEntity<WarnDTO> createWarn(@Valid WarnCreateDTO dto, UUID touristSpotId, UUID userId);

    @Operation(summary = "Buscar aviso", description = "Busca detalhes de um aviso pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aviso encontrado"),
            @ApiResponse(responseCode = "404", description = "Aviso não encontrado")
    })
    ResponseEntity<WarnDTO> getWarn(UUID warnId);

    @Operation(summary = "Listar avisos do ponto turístico", description = "Retorna uma página com todos os avisos de um ponto turístico específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    ResponseEntity<Page<WarnDTO>> getAllTouristSpotWarn(UUID touristSpotId, Pageable pageable);

    @Operation(summary = "Deletar aviso", description = "Exclui um aviso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Aviso excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aviso não encontrado")
    })
    ResponseEntity<Void> deleteWarn(UUID warnId, UUID userId);
}
