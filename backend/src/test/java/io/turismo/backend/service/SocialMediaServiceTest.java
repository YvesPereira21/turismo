package io.turismo.backend.service;

import io.turismo.backend.dto.social_media.SocialMediaCreateDTO;
import io.turismo.backend.dto.social_media.SocialMediaDTO;
import io.turismo.backend.dto.social_media.SocialMediaUpdateDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.mapper.SocialMediaMapper;
import io.turismo.backend.model.SocialMedia;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.model.enums.SocialMediaType;
import io.turismo.backend.repository.SocialMediaRepository;
import io.turismo.backend.repository.TouristSpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialMediaServiceTest {

    @Mock
    private SocialMediaRepository socialMediaRepository;
    @Mock
    private SocialMediaMapper socialMediaMapper;
    @Mock
    private TouristSpotRepository touristSpotRepository;

    @InjectMocks
    private SocialMediaService socialMediaService;

    private TouristSpot touristSpot;
    private SocialMedia socialMedia;
    private SocialMediaDTO socialMediaDTO;
    private SocialMediaCreateDTO socialMediaCreateDTO;
    private SocialMediaUpdateDTO socialMediaUpdateDTO;

    @BeforeEach
    void setUp() {
        touristSpot = new TouristSpot();
        ReflectionTestUtils.setField(touristSpot, "touristSpotId", UUID.randomUUID());

        socialMedia = new SocialMedia();
        ReflectionTestUtils.setField(socialMedia, "socialMediaId", UUID.randomUUID());
        socialMedia.setSocialMediaLink("https://instagram.com/spot");
        socialMedia.setSocialMediaType(SocialMediaType.INSTAGRAM);
        socialMedia.setTouristSpot(touristSpot);

        socialMediaDTO = new SocialMediaDTO(socialMedia.getSocialMediaId(), "https://instagram.com/spot", SocialMediaType.INSTAGRAM);
        socialMediaCreateDTO = new SocialMediaCreateDTO("https://instagram.com/spot", SocialMediaType.INSTAGRAM);
        socialMediaUpdateDTO = new SocialMediaUpdateDTO("https://instagram.com/spot_updated");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateSocialMedia() {
        UUID spotId = touristSpot.getTouristSpotId();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.of(touristSpot));
        when(socialMediaMapper.toEntity(socialMediaCreateDTO)).thenReturn(socialMedia);
        when(socialMediaRepository.save(any(SocialMedia.class))).thenReturn(socialMedia);
        when(socialMediaMapper.toDTO(socialMedia)).thenReturn(socialMediaDTO);

        SocialMediaDTO result = socialMediaService.createSocialMedia(socialMediaCreateDTO, spotId);

        assertNotNull(result);
        assertEquals(SocialMediaType.INSTAGRAM, result.socialMediaType());

        verify(touristSpotRepository, times(1)).findById(spotId);
        verify(socialMediaRepository, times(1)).save(any(SocialMedia.class));
    }

    @Test
    void shouldGetAllTouristSpotSocialsMedia() {
        UUID spotId = touristSpot.getTouristSpotId();

        when(socialMediaRepository.findAllByTouristSpot_TouristSpotId(spotId)).thenReturn(List.of(socialMedia));
        when(socialMediaMapper.toDTO(socialMedia)).thenReturn(socialMediaDTO);

        List<SocialMediaDTO> result = socialMediaService.getAllTouristSpotSocialsMedia(spotId);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(socialMediaRepository, times(1)).findAllByTouristSpot_TouristSpotId(spotId);
    }

    @Test
    void shouldUpdateSocialMedia() {
        UUID socialMediaId = socialMedia.getSocialMediaId();

        when(socialMediaRepository.findById(socialMediaId)).thenReturn(Optional.of(socialMedia));
        doNothing().when(socialMediaMapper).updateEntityFromDto(socialMediaUpdateDTO, socialMedia);
        when(socialMediaRepository.save(socialMedia)).thenReturn(socialMedia);
        when(socialMediaMapper.toDTO(socialMedia)).thenReturn(socialMediaDTO);

        SocialMediaDTO result = socialMediaService.updateSocialMedia(socialMediaUpdateDTO, socialMediaId);

        assertNotNull(result);

        verify(socialMediaRepository, times(1)).findById(socialMediaId);
        verify(socialMediaRepository, times(1)).save(socialMedia);
    }

    @Test
    void shouldDeleteSocialMedia() {
        UUID socialMediaId = socialMedia.getSocialMediaId();

        when(socialMediaRepository.findById(socialMediaId)).thenReturn(Optional.of(socialMedia));
        doNothing().when(socialMediaRepository).delete(socialMedia);

        assertDoesNotThrow(() -> socialMediaService.deleteSocialMedia(socialMediaId));

        verify(socialMediaRepository, times(1)).findById(socialMediaId);
        verify(socialMediaRepository, times(1)).delete(socialMedia);
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateSocialMediaAndThrowExceptionWhenTouristSpotNotFound() {
        UUID spotId = UUID.randomUUID();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> socialMediaService.createSocialMedia(socialMediaCreateDTO, spotId)
        );

        verify(touristSpotRepository, times(1)).findById(spotId);
        verify(socialMediaRepository, never()).save(any(SocialMedia.class));
    }

    @Test
    void shouldNotUpdateSocialMediaAndThrowExceptionWhenSocialMediaNotFound() {
        UUID socialMediaId = UUID.randomUUID();

        when(socialMediaRepository.findById(socialMediaId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> socialMediaService.updateSocialMedia(socialMediaUpdateDTO, socialMediaId)
        );

        verify(socialMediaRepository, times(1)).findById(socialMediaId);
        verify(socialMediaRepository, never()).save(any(SocialMedia.class));
    }

    @Test
    void shouldNotDeleteSocialMediaAndThrowExceptionWhenSocialMediaNotFound() {
        UUID socialMediaId = UUID.randomUUID();

        when(socialMediaRepository.findById(socialMediaId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> socialMediaService.deleteSocialMedia(socialMediaId)
        );

        verify(socialMediaRepository, times(1)).findById(socialMediaId);
        verify(socialMediaRepository, never()).delete(any(SocialMedia.class));
    }
}
