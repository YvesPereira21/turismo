package io.turismo.backend.controller;

import io.turismo.backend.controller.doc.ActivityDoc;
import io.turismo.backend.dto.activity.ActivityCreateDTO;
import io.turismo.backend.dto.activity.ActivityDTO;
import io.turismo.backend.service.ActivityService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class ActivityController implements ActivityDoc {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Override
    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PostMapping("/tourist-spots/{touristSpotId}/activities")
    public ResponseEntity<ActivityDTO> createActivity(
            @PathVariable UUID touristSpotId,
            @RequestBody ActivityCreateDTO dto,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        log.info("REST request to create activity for tourist spot ID: {}", touristSpotId);
        ActivityDTO created = activityService.createActivity(touristSpotId, userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/tourist-spots/{touristSpotId}/activities")
    public ResponseEntity<Set<ActivityDTO>> getActivitiesByTouristSpotId(
            @PathVariable UUID touristSpotId
    ) {
        log.info("REST request to get activities for tourist spot ID: {}", touristSpotId);
        Set<ActivityDTO> activities = activityService.getActivitiesByTouristSpotId(touristSpotId);
        return ResponseEntity.ok(activities);
    }

    @Override
    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PutMapping("/activities/{activityId}")
    public ResponseEntity<Void> updateActivity(
            @PathVariable UUID activityId,
            @RequestBody ActivityCreateDTO dto,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        log.info("REST request to update activity ID: {}", activityId);
        activityService.updateActivity(activityId, userId, dto);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('SPOTMANAGER', 'ADMIN')")
    @DeleteMapping("/activities/{activityId}")
    public ResponseEntity<Void> deleteActivity(
            @PathVariable UUID activityId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        log.info("REST request to delete activity ID: {}", activityId);
        activityService.deleteActivity(activityId, userId);
        return ResponseEntity.noContent().build();
    }
}
