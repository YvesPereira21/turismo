package io.turismo.backend.controller;

import io.turismo.backend.controller.docs.TourGuideDocs;
import io.turismo.backend.dto.tour_guide.TourGuideCreateDTO;
import io.turismo.backend.dto.tour_guide.TourGuideDTO;
import io.turismo.backend.dto.tour_guide.TourGuideUpdateDTO;
import io.turismo.backend.service.TourGuideService;
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
public class TourGuideController implements TourGuideDocs {

    private final TourGuideService tourGuideService;

    public TourGuideController(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    @Override
    @PostMapping("/tour-guides")
    public ResponseEntity<TourGuideDTO> createTourGuide(@RequestBody TourGuideCreateDTO dto) {
        TourGuideDTO created = tourGuideService.createTourGuide(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @GetMapping("/tour-guides/{tourGuideId}")
    public ResponseEntity<TourGuideDTO> getTourGuide(@PathVariable UUID tourGuideId) {
        TourGuideDTO guide = tourGuideService.getTourGuide(tourGuideId);
        return ResponseEntity.ok(guide);
    }

    @Override
    @PreAuthorize("hasRole('TOURGUIDE')")
    @PutMapping("/tour-guides/{tourGuideId}")
    public ResponseEntity<TourGuideDTO> updateTourGuide(
            @RequestBody TourGuideUpdateDTO dto,
            @PathVariable UUID tourGuideId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        TourGuideDTO updated = tourGuideService.updateTourGuide(dto, tourGuideId, userId);
        return ResponseEntity.ok(updated);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'TOURGUIDE')")
    @DeleteMapping("/tour-guides/{tourGuideId}")
    public ResponseEntity<Void> deleteTourGuide(
            @PathVariable UUID tourGuideId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        tourGuideService.deleteTourGuide(tourGuideId, userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/tourist-spots/{touristSpotId}/tour-guides")
    public ResponseEntity<Page<TourGuideDTO>> getTourGuidesByTouristSpot(
            @PathVariable UUID touristSpotId,
            @PageableDefault(page = 0, size = 10, sort = "user.name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<TourGuideDTO> guides = tourGuideService.getTourGuidesByTouristSpot(touristSpotId, pageable);
        return ResponseEntity.ok(guides);
    }
}
