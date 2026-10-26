package io.turismo.backend.controller;

import io.turismo.backend.controller.docs.TouristDocs;
import io.turismo.backend.dto.tourist.TouristCreateDTO;
import io.turismo.backend.dto.tourist.TouristDTO;
import io.turismo.backend.dto.tourist.TouristUpdateDTO;
import io.turismo.backend.service.TouristService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class TouristController implements TouristDocs {

    private final TouristService touristService;

    public TouristController(TouristService touristService) {
        this.touristService = touristService;
    }

    @Override
    @PostMapping("/tourists")
    public ResponseEntity<TouristDTO> createTourist(@RequestBody TouristCreateDTO dto) {
        log.info("REST request to create tourist");
        TouristDTO created = touristService.createTourist(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'TOURIST')")
    @GetMapping("/tourists/{touristId}")
    public ResponseEntity<TouristDTO> getTourist(@PathVariable UUID touristId) {
        log.info("REST request to get tourist ID: {}", touristId);
        TouristDTO tourist = touristService.getTourist(touristId);
        return ResponseEntity.ok(tourist);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'TOURIST')")
    @PutMapping("/tourists/{touristId}")
    public ResponseEntity<TouristDTO> updateTourist(
            @RequestBody TouristUpdateDTO dto,
            @PathVariable UUID touristId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        log.info("REST request to update tourist ID: {}", touristId);
        TouristDTO updated = touristService.updateTourist(dto, touristId, userId);
        return ResponseEntity.ok(updated);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'TOURIST')")
    @DeleteMapping("/tourists/{touristId}")
    public ResponseEntity<Void> deleteTourist(
            @PathVariable UUID touristId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        log.info("REST request to delete tourist ID: {}", touristId);
        touristService.deleteTourist(touristId, userId);
        return ResponseEntity.noContent().build();
    }
}
