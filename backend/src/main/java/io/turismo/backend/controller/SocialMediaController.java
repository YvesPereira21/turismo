package io.turismo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import io.turismo.backend.dto.social_media.SocialMediaDTO;
import io.turismo.backend.dto.social_media.SocialMediaUpdateDTO;
import io.turismo.backend.service.SocialMediaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1")
@Validated
@Tag(name = "Rede Social", description = "Endpoints para gerenciamento de redes sociais de gerentes")
public class SocialMediaController {

    private final SocialMediaService socialMediaService;

    public SocialMediaController(SocialMediaService socialMediaService) {
        this.socialMediaService = socialMediaService;
    }

    @PostMapping("/manager/{spotManagerId}/social-medias")
    @Operation(summary = "Criar rede social", description = "Cria uma nova rede social para um gerente de ponto turístico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rede social criada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado")
    })
    public ResponseEntity<SocialMediaDTO> createSocialMedia(
            @Valid @RequestBody SocialMediaCreateDTO dto,
            @PathVariable UUID spotManagerId
    ) {
        SocialMediaDTO created = socialMediaService.createSocialMedia(dto, spotManagerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/manager/{spotManagerId}/social-medias")
    @Operation(summary = "Listar redes sociais do gerente", description = "Busca todas as redes sociais cadastradas de um gerente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<List<SocialMediaDTO>> getAllSpotManagerSocialsMedia(@PathVariable UUID spotManagerId) {
        List<SocialMediaDTO> socials = socialMediaService.getAllSpotManagerSocialsMedia(spotManagerId);
        return ResponseEntity.ok(socials);
    }

    @PutMapping("/social-medias/{socialMediaId}")
    @Operation(summary = "Atualizar rede social", description = "Atualiza os dados de uma rede social existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rede social atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Rede social não encontrada")
    })
    public ResponseEntity<SocialMediaDTO> updateSocialMedia(
            @Valid @RequestBody SocialMediaUpdateDTO socialMediaUpdateDTO,
            @PathVariable UUID socialMediaId
    ) {
        SocialMediaDTO updated = socialMediaService.updateSocialMedia(socialMediaUpdateDTO, socialMediaId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/social-medias/{socialMediaId}")
    @Operation(summary = "Deletar rede social", description = "Exclui uma rede social")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rede social excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Rede social não encontrada")
    })
    public ResponseEntity<Void> deleteSocialMedia(@PathVariable UUID socialMediaId) {
        socialMediaService.deleteSocialMedia(socialMediaId);
        return ResponseEntity.noContent().build();
    }
}
