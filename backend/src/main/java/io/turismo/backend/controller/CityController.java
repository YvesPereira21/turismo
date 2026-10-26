package io.turismo.backend.controller;

import io.turismo.backend.controller.doc.CityDoc;
import io.turismo.backend.dto.city.CityCreateDTO;
import io.turismo.backend.dto.city.CityDTO;
import io.turismo.backend.service.CityService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class CityController implements CityDoc {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cities")
    public ResponseEntity<CityDTO> createCity(@RequestBody CityCreateDTO dto) {
        log.info("REST request to create city: {}", dto.name());
        CityDTO created = cityService.createCity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @GetMapping("/cities/{cityName}/{stateName}")
    public ResponseEntity<CityDTO> getCity(@PathVariable String cityName, @PathVariable String stateName) {
        log.info("REST request to get city: {} in state: {}", cityName, stateName);
        CityDTO city = cityService.getCity(cityName, stateName);
        return ResponseEntity.ok(city);
    }

    @Override
    @GetMapping("/state/{stateName}/cities")
    public ResponseEntity<List<CityDTO>> getCitiesFromState(@PathVariable String stateName) {
        log.info("REST request to get cities from state: {}", stateName);
        List<CityDTO> cities = cityService.getCitiesFromState(stateName);
        return ResponseEntity.ok(cities);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cities/{cityId}")
    public ResponseEntity<Void> deleteCity(@PathVariable UUID cityId) {
        log.info("REST request to delete city ID: {}", cityId);
        cityService.deleteCity(cityId);
        return ResponseEntity.noContent().build();
    }
}
