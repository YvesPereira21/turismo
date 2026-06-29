package io.turismo.backend.service;

import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import io.turismo.backend.dto.social_media.SocialMediaDTO;
import io.turismo.backend.dto.social_media.SocialMediaUpdateDTO;
import io.turismo.backend.mapper.SocialMediaMapper;
import io.turismo.backend.model.SocialMedia;
import io.turismo.backend.model.SpotManager;
import io.turismo.backend.repository.SocialMediaRepository;
import io.turismo.backend.repository.SpotManagerRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class SocialMediaService {
    private final SocialMediaRepository socialMediaRepository;
    private final SocialMediaMapper socialMediaMapper;
    private final SpotManagerRepository spotManagerRepository;

    public SocialMediaService(SocialMediaRepository socialMediaRepository, SocialMediaMapper socialMediaMapper, SpotManagerRepository spotManagerRepository) {
        this.socialMediaRepository = socialMediaRepository;
        this.socialMediaMapper = socialMediaMapper;
        this.spotManagerRepository = spotManagerRepository;
    }

    public SocialMediaDTO createSocialMedia(SocialMediaCreateDTO dto, UUID spotManagerId) {
        SpotManager spotManager = spotManagerRepository.findById(spotManagerId)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));

        SocialMedia socialMedia = socialMediaMapper.toEntity(dto);
        socialMedia.setSpotManager(spotManager);

        return socialMediaMapper.toDTO(socialMediaRepository.save(socialMedia));
    }

    public List<SocialMediaDTO> getAllSpotManagerSocialsMedia(UUID spotManagerId){
        return socialMediaRepository.findAllBySpotManager_SpotManagerId(spotManagerId)
                .stream()
                .map(socialMediaMapper::toDTO)
                .toList();
    }

    public SocialMediaDTO updateSocialMedia(SocialMediaUpdateDTO socialMediaUpdateDTO, UUID socialMediaId){
        SocialMedia socialMedia = socialMediaRepository.findById(socialMediaId)
                .orElseThrow(() -> new RuntimeException("Rede social não encontrada"));

        socialMediaMapper.updateEntityFromDto(socialMediaUpdateDTO, socialMedia);

        return socialMediaMapper.toDTO(socialMediaRepository.save(socialMedia));
    }

    public void deleteSocialMedia(UUID socialMediaId) {
        SocialMedia socialMedia = socialMediaRepository.findById(socialMediaId)
                .orElseThrow(() -> new RuntimeException("Rede social não encontrada"));

        socialMediaRepository.delete(socialMedia);
    }
}
