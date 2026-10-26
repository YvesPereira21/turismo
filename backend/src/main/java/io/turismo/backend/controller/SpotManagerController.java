package io.turismo.backend.controller;

import io.turismo.backend.controller.docs.SpotManagerDocs;
import io.turismo.backend.dto.spot_manager.SpotManagerCreateDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerSimpleDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerUpdateDTO;
import io.turismo.backend.service.SpotManagerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class SpotManagerController implements SpotManagerDocs {

    private final SpotManagerService spotManagerService;

    public SpotManagerController(SpotManagerService spotManagerService) {
        this.spotManagerService = spotManagerService;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/spot-managers")
    public ResponseEntity<SpotManagerSimpleDTO> createSpotManager(@RequestBody SpotManagerCreateDTO dto) {
        SpotManagerSimpleDTO created = spotManagerService.createSpotManager(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @GetMapping("/spot-managers/{spotManagerId}")
    public ResponseEntity<SpotManagerSimpleDTO> getSpotManager(@PathVariable UUID spotManagerId) {
        SpotManagerSimpleDTO manager = spotManagerService.getSpotManager(spotManagerId);
        return ResponseEntity.ok(manager);
    }

    @Override
    @GetMapping("/spot-managers/{spotManagerId}/current")
    public ResponseEntity<SpotManagerDTO> currentSpotManager(@PathVariable UUID spotManagerId) {
        SpotManagerDTO manager = spotManagerService.currentSpotManager(spotManagerId);
        return ResponseEntity.ok(manager);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'SPOTMANAGER')")
    @PutMapping("/spot-managers/{spotManagerId}")
    public ResponseEntity<SpotManagerSimpleDTO> updateSpotManager(
            @RequestBody SpotManagerUpdateDTO spotManagerUpdateDTO,
            @PathVariable UUID spotManagerId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        SpotManagerSimpleDTO updated = spotManagerService.updateSpotManager(spotManagerUpdateDTO, spotManagerId, userId);
        return ResponseEntity.ok(updated);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'SPOTMANAGER')")
    @DeleteMapping("/spot-managers/{spotManagerId}")
    public ResponseEntity<Void> deleteSpotManager(
            @PathVariable UUID spotManagerId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        spotManagerService.deleteSpotManager(spotManagerId, userId);
        return ResponseEntity.noContent().build();
    }
}
