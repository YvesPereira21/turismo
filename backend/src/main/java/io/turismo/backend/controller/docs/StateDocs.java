package io.turismo.backend.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.state.StateCreateDTO;
import io.turismo.backend.dto.state.StateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

@Tag(name = "Estado", description = "Endpoints para gerenciamento de estados brasileiros")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface StateDocs {

    @Operation(summary = "Criar estado", description = "Cadastra um novo estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estado criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nome do estado já existe ou dados inválidos")
    })
    ResponseEntity<Void> createState(@Valid StateCreateDTO dto);

    @Operation(summary = "Listar estados", description = "Retorna todos os estados cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    ResponseEntity<Set<StateDTO>> getAllStates();

    @Operation(summary = "Deletar estado", description = "Exclui um estado pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estado excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Estado não encontrado")
    })
    ResponseEntity<Void> deleteState(UUID stateId);
}
