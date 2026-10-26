package io.turismo.backend.controller.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import io.turismo.backend.dto.social_media.SocialMediaDTO;
import io.turismo.backend.dto.social_media.SocialMediaUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Rede Social", description = "Endpoints para gerenciamento de redes sociais de gerentes")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface SocialMediaDoc {

    @Operation(summary = "Criar rede social", description = "Cria uma nova rede social para um ponto turístico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rede social criada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ponto Turístico não encontrado")
    })
    ResponseEntity<SocialMediaDTO> createSocialMedia(@Valid SocialMediaCreateDTO dto, UUID touristSpotId);

    @Operation(summary = "Listar redes sociais do ponto turístico", description = "Busca todas as redes sociais cadastradas de um ponto turístico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    ResponseEntity<List<SocialMediaDTO>> getAllTouristSpotSocialsMedia(UUID touristSpotId);

    @Operation(summary = "Atualizar rede social", description = "Atualiza os dados de uma rede social existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rede social atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Rede social não encontrada")
    })
    ResponseEntity<SocialMediaDTO> updateSocialMedia(@Valid SocialMediaUpdateDTO socialMediaUpdateDTO, UUID socialMediaId);

    @Operation(summary = "Deletar rede social", description = "Exclui uma rede social")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rede social excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Rede social não encontrada")
    })
    ResponseEntity<Void> deleteSocialMedia(UUID socialMediaId);
}
