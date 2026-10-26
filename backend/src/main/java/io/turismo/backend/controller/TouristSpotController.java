package io.turismo.backend.controller;

import io.turismo.backend.controller.docs.TouristSpotDocs;
import io.turismo.backend.dto.geojson.GeoFeatureCollectionDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotCreateDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotListDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotToMapDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotUpdateDTO;
import io.turismo.backend.service.TouristSpotService;
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

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class TouristSpotController implements TouristSpotDocs {

    private final TouristSpotService touristSpotService;

    public TouristSpotController(TouristSpotService touristSpotService) {
        this.touristSpotService = touristSpotService;
    }

    @Override
    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PostMapping("/tourist-spots")
    public ResponseEntity<TouristSpotDTO> createTouristSpot(
            @RequestBody TouristSpotCreateDTO dto,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        TouristSpotDTO created = touristSpotService.createTouristSpot(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @GetMapping("/tourist-spots/{touristSpotId}")
    public ResponseEntity<TouristSpotDTO> getTouristSpot(@PathVariable UUID touristSpotId) {
        TouristSpotDTO spot = touristSpotService.getTouristSpot(touristSpotId);
        return ResponseEntity.ok(spot);
    }

    @Override
    @GetMapping("/spots-to-map/")
    public ResponseEntity<GeoFeatureCollectionDTO<TouristSpotToMapDTO>> getTouristSpotsToMap() {
        return ResponseEntity.ok(touristSpotService.getTouristSpotsToMap());
    }

    @Override
    @GetMapping("/tourist-spots")
    public ResponseEntity<Page<TouristSpotListDTO>> getTouristSpots(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String cityName,
            @RequestParam(required = false) String stateName,
            @RequestParam(required = false) Set<String> tags,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double radius,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TouristSpotListDTO> spots = touristSpotService.getTouristSpots(
                name, cityName, stateName, tags, longitude, latitude, radius, pageable
        );
        return ResponseEntity.ok(spots);
    }

    @Override
    @GetMapping("/manager/{spotManagerId}/all-tourist-spots")
    public ResponseEntity<Page<TouristSpotListDTO>> getSpotManagerTouristSpots(
            @PathVariable UUID spotManagerId,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TouristSpotListDTO> spots = touristSpotService.getSpotManagerTouristSpots(spotManagerId, pageable);
        return ResponseEntity.ok(spots);
    }

    @Override
    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PutMapping("/tourist-spots/{touristSpotId}")
    public ResponseEntity<Void> updateTouristSpot(
            @PathVariable UUID touristSpotId,
            @RequestBody TouristSpotUpdateDTO dto,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        touristSpotService.updateTouristSpot(touristSpotId, dto, userId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'SPOTMANAGER')")
    @DeleteMapping("/tourist-spots/{touristSpotId}")
    public ResponseEntity<Void> deleteTouristSpot(
            @PathVariable UUID touristSpotId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        touristSpotService.deleteTouristSpot(touristSpotId, userId);
        return ResponseEntity.noContent().build();
    }
}
