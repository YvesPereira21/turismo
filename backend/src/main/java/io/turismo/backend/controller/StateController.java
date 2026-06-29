package io.turismo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.dto.state.StateCreateDTO;
import io.turismo.backend.dto.state.StateDTO;
import io.turismo.backend.service.StateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1")
@Validated
@Tag(name = "Estado", description = "Endpoints para gerenciamento de estados brasileiros")
public class StateController {

    private final StateService stateService;

    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @PostMapping("/states")
    @Operation(summary = "Criar estado", description = "Cadastra um novo estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estado criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nome do estado já existe ou dados inválidos")
    })
    public ResponseEntity<Void> createState(@Valid @RequestBody StateCreateDTO dto) {
        stateService.createState(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/states")
    @Operation(summary = "Listar estados", description = "Retorna todos os estados cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Set<StateDTO>> getAllStates() {
        Set<StateDTO> states = stateService.getAllStates();
        return ResponseEntity.ok(states);
    }

    @DeleteMapping("/states/{stateId}")
    @Operation(summary = "Deletar estado", description = "Exclui um estado pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estado excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Estado não encontrado")
    })
    public ResponseEntity<Void> deleteState(@PathVariable UUID stateId) {
        stateService.deleteState(stateId);
        return ResponseEntity.noContent().build();
    }
}
