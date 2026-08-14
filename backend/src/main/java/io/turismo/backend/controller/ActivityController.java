package io.turismo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
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
@SecurityRequirement(name = SecurityConfig.SECURITY)
@RequestMapping(path = "/api/v1")
@Validated
@Tag(name = "Atividade", description = "Endpoints para gerenciamento das atividades dos pontos turísticos")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PostMapping("/tourist-spots/{touristSpotId}/activities")
    @Operation(summary = "Criar atividade", description = "Cria uma nova atividade para um ponto turístico (Apenas gerentes)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Atividade criada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    public ResponseEntity<ActivityDTO> createActivity(
            @PathVariable UUID touristSpotId,
            @Valid @RequestBody ActivityCreateDTO dto,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        log.info("REST request to create activity for tourist spot ID: {}", touristSpotId);
        ActivityDTO created = activityService.createActivity(touristSpotId, userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/tourist-spots/{touristSpotId}/activities")
    @Operation(summary = "Listar atividades", description = "Retorna a lista de atividades de um ponto turístico (Requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    public ResponseEntity<Set<ActivityDTO>> getActivitiesByTouristSpotId(
            @PathVariable UUID touristSpotId
    ) {
        log.info("REST request to get activities for tourist spot ID: {}", touristSpotId);
        Set<ActivityDTO> activities = activityService.getActivitiesByTouristSpotId(touristSpotId);
        return ResponseEntity.ok(activities);
    }

    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PutMapping("/activities/{activityId}")
    @Operation(summary = "Atualizar atividade", description = "Atualiza os dados de uma atividade (Apenas o gerente dono do ponto turístico)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atividade atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    public ResponseEntity<Void> updateActivity(
            @PathVariable UUID activityId,
            @Valid @RequestBody ActivityCreateDTO dto,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        log.info("REST request to update activity ID: {}", activityId);
        activityService.updateActivity(activityId, userId, dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('SPOTMANAGER', 'ADMIN')")
    @DeleteMapping("/activities/{activityId}")
    @Operation(summary = "Deletar atividade", description = "Remove uma atividade (Requer ser o dono do ponto turístico ou Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Atividade removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    public ResponseEntity<Void> deleteActivity(
            @PathVariable UUID activityId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        log.info("REST request to delete activity ID: {}", activityId);
        activityService.deleteActivity(activityId, userId);
        return ResponseEntity.noContent().build();
    }
}
