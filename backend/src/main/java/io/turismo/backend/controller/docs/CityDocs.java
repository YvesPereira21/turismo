package io.turismo.backend.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.city.CityCreateDTO;
import io.turismo.backend.dto.city.CityDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Cidade", description = "Endpoints para gerenciamento de cidades")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface CityDocs {

    @Operation(summary = "Criar cidade", description = "Cria uma nova cidade vinculada a um estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cidade criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos ou cidade já existe")
    })
    ResponseEntity<CityDTO> createCity(@Valid CityCreateDTO dto);

    @Operation(summary = "Buscar cidade", description = "Busca uma cidade pelo seu nome e pelo nome do seu estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Cidade não encontrada")
    })
    ResponseEntity<CityDTO> getCity(String cityName, String stateName);

    @Operation(summary = "Listar cidades de um estado", description = "Retorna todas as cidades pertencentes a um estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    })
    ResponseEntity<List<CityDTO>> getCitiesFromState(String stateName);

    @Operation(summary = "Excluir cidade", description = "Exclui uma cidade pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cidade excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cidade não encontrada")
    })
    ResponseEntity<Void> deleteCity(UUID cityId);
}
