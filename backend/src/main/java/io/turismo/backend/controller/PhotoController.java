package io.turismo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1")
@Validated
@Tag(name = "Foto", description = "Endpoints para upload e gerenciamento de fotos de pontos turísticos e atividades")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping(value = "/tourist-spots/{touristSpotId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Fazer upload de fotos do ponto turístico", description = "Faz o upload de uma ou mais fotos para um ponto turístico específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upload realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Imagens inválidas fornecidas"),
            @ApiResponse(responseCode = "404", description = "Ponto turístico não encontrado")
    })
    public ResponseEntity<Void> uploadTouristSpotsPhotos(
            @PathVariable UUID touristSpotId,
            @RequestParam("photos") List<MultipartFile> photos
    ) throws IOException {
        photoService.uploadTouristSpotsPhotos(touristSpotId, photos);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/activities/{activityId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Fazer upload de foto da atividade", description = "Faz o upload de uma foto para uma atividade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upload realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Imagem inválida fornecida"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    public ResponseEntity<Void> uploadActivityPhoto(
            @PathVariable UUID activityId,
            @RequestParam("photo") MultipartFile photo
    ) throws IOException {
        photoService.uploadActivityPhoto(activityId, photo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/activities/{activityId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Atualizar foto da atividade", description = "Atualiza (substitui) a foto de uma atividade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Imagem inválida fornecida"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada")
    })
    public ResponseEntity<Void> updateActivityPhoto(
            @PathVariable UUID activityId,
            @RequestParam("photo") MultipartFile photo
    ) throws IOException {
        photoService.updateActivityPhoto(activityId, photo);
        return ResponseEntity.ok().build();
    }
}
