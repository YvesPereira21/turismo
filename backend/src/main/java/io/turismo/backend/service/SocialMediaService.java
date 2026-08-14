package io.turismo.backend.service;

import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import io.turismo.backend.dto.social_media.SocialMediaDTO;
import io.turismo.backend.dto.social_media.SocialMediaUpdateDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.mapper.SocialMediaMapper;
import io.turismo.backend.model.SocialMedia;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.repository.SocialMediaRepository;
import io.turismo.backend.repository.TouristSpotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class SocialMediaService {
    private final SocialMediaRepository socialMediaRepository;
    private final SocialMediaMapper socialMediaMapper;
    private final TouristSpotRepository touristSpotRepository;

    public SocialMediaService(SocialMediaRepository socialMediaRepository, SocialMediaMapper socialMediaMapper, TouristSpotRepository touristSpotRepository) {
        this.socialMediaRepository = socialMediaRepository;
        this.socialMediaMapper = socialMediaMapper;
        this.touristSpotRepository = touristSpotRepository;
    }

    public SocialMediaDTO createSocialMedia(SocialMediaCreateDTO dto, UUID touristSpotId) {
        log.info("Creating social media for tourist spot ID: {}", touristSpotId);
        TouristSpot touristSpot = touristSpotRepository.findById(touristSpotId)
                .orElseThrow(() -> new ObjectNotFoundException("Ponto Turístico não encontrado"));

        SocialMedia socialMedia = socialMediaMapper.toEntity(dto);
        socialMedia.setTouristSpot(touristSpot);

        SocialMedia saved = socialMediaRepository.save(socialMedia);
        log.info("Social media created with ID: {}", saved.getSocialMediaId());
        return socialMediaMapper.toDTO(saved);
    }

    public List<SocialMediaDTO> getAllTouristSpotSocialsMedia(UUID touristSpotId){
        log.info("Fetching socials media for tourist spot ID: {}", touristSpotId);
        return socialMediaRepository.findAllByTouristSpot_TouristSpotId(touristSpotId)
                .stream()
                .map(socialMediaMapper::toDTO)
                .toList();
    }

    public SocialMediaDTO updateSocialMedia(SocialMediaUpdateDTO socialMediaUpdateDTO, UUID socialMediaId){
        log.info("Updating social media ID: {}", socialMediaId);
        SocialMedia socialMedia = socialMediaRepository.findById(socialMediaId)
                .orElseThrow(() -> new ObjectNotFoundException("Rede social não encontrada"));

        socialMediaMapper.updateEntityFromDto(socialMediaUpdateDTO, socialMedia);

        return socialMediaMapper.toDTO(socialMediaRepository.save(socialMedia));
    }

    public void deleteSocialMedia(UUID socialMediaId) {
        log.info("Deleting social media ID: {}", socialMediaId);
        SocialMedia socialMedia = socialMediaRepository.findById(socialMediaId)
                .orElseThrow(() -> new ObjectNotFoundException("Rede social não encontrada"));

        socialMediaRepository.delete(socialMedia);
    }
}
