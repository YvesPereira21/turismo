package io.turismo.backend.controller;

import io.turismo.backend.controller.doc.WarnDoc;
import io.turismo.backend.dto.warn.WarnCreateDTO;
import io.turismo.backend.dto.warn.WarnDTO;
import io.turismo.backend.service.WarnService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
public class WarnController implements WarnDoc {

    private final WarnService warnService;

    public WarnController(WarnService warnService) {
        this.warnService = warnService;
    }

    @Override
    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PostMapping("/warns/spot/{touristSpotId}")
    public ResponseEntity<WarnDTO> createWarn(
            @RequestBody WarnCreateDTO dto,
            @PathVariable UUID touristSpotId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        WarnDTO created = warnService.createWarn(userId, dto, touristSpotId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @GetMapping("/warns/{warnId}")
    public ResponseEntity<WarnDTO> getWarn(@PathVariable UUID warnId) {
        WarnDTO warn = warnService.getWarn(warnId);
        return ResponseEntity.ok(warn);
    }

    @Override
    @GetMapping("/warns/spot/{touristSpotId}")
    public ResponseEntity<Page<WarnDTO>> getAllTouristSpotWarn(
            @PathVariable UUID touristSpotId,
            @PageableDefault(page = 0, size = 10, sort = "eventDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<WarnDTO> warns = warnService.getAllTouristSpotWarn(touristSpotId, pageable);
        return ResponseEntity.ok(warns);
    }

    @Override
    @PreAuthorize("hasRole('SPOTMANAGER')")
    @DeleteMapping("/warns/{warnId}")
    public ResponseEntity<Void> deleteWarn(
            @PathVariable UUID warnId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        warnService.deleteWarn(userId, warnId);
        return ResponseEntity.noContent().build();
    }
}
