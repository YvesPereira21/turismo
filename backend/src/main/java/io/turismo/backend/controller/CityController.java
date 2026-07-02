package io.turismo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.dto.city.CityCreateDTO;
import io.turismo.backend.dto.city.CityDTO;
import io.turismo.backend.service.CityService;
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
@Tag(name = "Cidade", description = "Endpoints para gerenciamento de cidades")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping("/cities")
    @Operation(summary = "Criar cidade", description = "Cria uma nova cidade vinculada a um estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cidade criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos ou cidade já existe")
    })
    public ResponseEntity<CityDTO> createCity(@Valid @RequestBody CityCreateDTO dto) {
        CityDTO created = cityService.createCity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/cities/{cityName}/{stateName}")
    @Operation(summary = "Buscar cidade", description = "Busca uma cidade pelo seu nome e pelo nome do seu estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Cidade não encontrada")
    })
    public ResponseEntity<CityDTO> getCity(@PathVariable String cityName, @PathVariable String stateName) {
        CityDTO city = cityService.getCity(cityName, stateName);
        return ResponseEntity.ok(city);
    }

    @GetMapping("/state/{stateName}/cities")
    @Operation(summary = "Listar cidades de um estado", description = "Retorna uma página com as cidades pertencentes a um estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    })
    public ResponseEntity<Page<CityDTO>> getCitiesFromState(
            @PathVariable String stateName,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CityDTO> cities = cityService.getCitiesFromState(stateName, pageable);
        return ResponseEntity.ok(cities);
    }

    @DeleteMapping("/cities/{cityId}")
    @Operation(summary = "Excluir cidade", description = "Exclui uma cidade pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cidade excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cidade não encontrada")
    })
    public ResponseEntity<Void> deleteCity(@PathVariable UUID cityId) {
        cityService.deleteCity(cityId);
        return ResponseEntity.noContent().build();
    }
}
