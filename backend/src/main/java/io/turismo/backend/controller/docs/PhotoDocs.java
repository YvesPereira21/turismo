package io.turismo.backend.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.photo.PhotoUploadDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "Foto", description = "Endpoints para upload e gerenciamento de fotos de pontos turísticos e atividades")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface PhotoDocs {

    @Operation(summary = "Fazer upload de fotos do ponto turístico", description = "Faz o upload de uma ou mais fotos para um ponto turístico específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upload realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Imagens inválidas fornecidas"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    ResponseEntity<Void> uploadTouristSpotsPhotos(UUID touristSpotId, @Valid PhotoUploadDTO dto) throws IOException;

    @Operation(summary = "Fazer upload de foto da atividade", description = "Faz o upload de uma foto para uma atividade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upload realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Imagem inválida fornecida"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    ResponseEntity<Void> uploadActivityPhoto(UUID activityId, MultipartFile photo) throws IOException;

    @Operation(summary = "Atualizar foto da atividade", description = "Atualiza (substitui) a foto de uma atividade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Imagem inválida fornecida"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    ResponseEntity<Void> updateActivityPhoto(UUID activityId, MultipartFile photo) throws IOException;
}
