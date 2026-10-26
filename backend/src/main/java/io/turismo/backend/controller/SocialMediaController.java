package io.turismo.backend.controller;

import io.turismo.backend.controller.docs.SocialMediaDocs;
import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import io.turismo.backend.dto.social_media.SocialMediaDTO;
import io.turismo.backend.dto.social_media.SocialMediaUpdateDTO;
import io.turismo.backend.service.SocialMediaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class SocialMediaController implements SocialMediaDocs {

    private final SocialMediaService socialMediaService;

    public SocialMediaController(SocialMediaService socialMediaService) {
        this.socialMediaService = socialMediaService;
    }

    @Override
    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PostMapping("/tourist-spot/{touristSpotId}/social-medias")
    public ResponseEntity<SocialMediaDTO> createSocialMedia(
            @RequestBody SocialMediaCreateDTO dto,
            @PathVariable UUID touristSpotId
    ) {
        SocialMediaDTO created = socialMediaService.createSocialMedia(dto, touristSpotId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @GetMapping("/tourist-spot/{touristSpotId}/social-medias")
    public ResponseEntity<List<SocialMediaDTO>> getAllTouristSpotSocialsMedia(@PathVariable UUID touristSpotId) {
        List<SocialMediaDTO> socials = socialMediaService.getAllTouristSpotSocialsMedia(touristSpotId);
        return ResponseEntity.ok(socials);
    }

    @Override
    @PreAuthorize("hasRole('SPOTMANAGER')")
    @PutMapping("/social-medias/{socialMediaId}")
    public ResponseEntity<SocialMediaDTO> updateSocialMedia(
            @RequestBody SocialMediaUpdateDTO socialMediaUpdateDTO,
            @PathVariable UUID socialMediaId
    ) {
        SocialMediaDTO updated = socialMediaService.updateSocialMedia(socialMediaUpdateDTO, socialMediaId);
        return ResponseEntity.ok(updated);
    }

    @Override
    @PreAuthorize("hasAnyRole('SPOTMANAGER', 'ADMIN')")
    @DeleteMapping("/social-medias/{socialMediaId}")
    public ResponseEntity<Void> deleteSocialMedia(@PathVariable UUID socialMediaId) {
        socialMediaService.deleteSocialMedia(socialMediaId);
        return ResponseEntity.noContent().build();
    }
}
