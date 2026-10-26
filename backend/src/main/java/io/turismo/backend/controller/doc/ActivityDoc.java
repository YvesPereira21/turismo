package io.turismo.backend.controller.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.activity.ActivityCreateDTO;
import io.turismo.backend.dto.activity.ActivityDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

@Tag(name = "Atividade", description = "Endpoints para gerenciamento das atividades dos pontos turísticos")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface ActivityDoc {

    @Operation(summary = "Criar atividade", description = "Cria uma nova atividade para um ponto turístico (Apenas gerentes)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Atividade criada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    ResponseEntity<ActivityDTO> createActivity(UUID touristSpotId, @Valid ActivityCreateDTO dto, UUID userId);

    @Operation(summary = "Listar atividades", description = "Retorna a lista de atividades de um ponto turístico (Requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    ResponseEntity<Set<ActivityDTO>> getActivitiesByTouristSpotId(UUID touristSpotId);

    @Operation(summary = "Atualizar atividade", description = "Atualiza os dados de uma atividade (Apenas o gerente dono do ponto turístico)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atividade atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    ResponseEntity<Void> updateActivity(UUID activityId, @Valid ActivityCreateDTO dto, UUID userId);

    @Operation(summary = "Deletar atividade", description = "Remove uma atividade (Requer ser o dono do ponto turístico ou Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Atividade removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    ResponseEntity<Void> deleteActivity(UUID activityId, UUID userId);
}
