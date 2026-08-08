package io.turismo.backend.service;

import io.turismo.backend.dto.tour_guide.TourGuideCreateDTO;
import io.turismo.backend.dto.tour_guide.TourGuideDTO;
import io.turismo.backend.dto.tour_guide.TourGuideUpdateDTO;
import io.turismo.backend.dto.user.UserCreateDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.mapper.TourGuideMapper;
import io.turismo.backend.model.TourGuide;
import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.TourGuideType;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.TourGuideRepository;
import io.turismo.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourGuideServiceTest {

    @Mock
    private TourGuideRepository tourGuideRepository;
    @Mock
    private TourGuideMapper tourGuideMapper;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private TourGuideService tourGuideService;

    private User userOwner;
    private User adminUser;
    private User otherUser;
    private TourGuide tourGuide;
    private TourGuideDTO tourGuideDTO;
    private TourGuideCreateDTO tourGuideCreateDTO;
    private TourGuideUpdateDTO tourGuideUpdateDTO;

    @BeforeEach
    void setUp() {
        userOwner = new User();
        ReflectionTestUtils.setField(userOwner, "id", UUID.randomUUID());
        userOwner.setEmail("guide@gmail.com");
        userOwner.setName("Guide");
        userOwner.setPhone("999999999");
        userOwner.setRole(UserRole.TOURGUIDE);

        adminUser = new User();
        ReflectionTestUtils.setField(adminUser, "id", UUID.randomUUID());
        adminUser.setRole(UserRole.ADMIN);

        otherUser = new User();
        ReflectionTestUtils.setField(otherUser, "id", UUID.randomUUID());
        otherUser.setRole(UserRole.TOURGUIDE);

        tourGuide = new TourGuide();
        ReflectionTestUtils.setField(tourGuide, "tourGuideId", UUID.randomUUID());
        tourGuide.setUser(userOwner);
        tourGuide.setCadastur("CAD123456");

        UserCreateDTO userCreateDTO = new UserCreateDTO("Guide", "guide@gmail.com", "pass123", "999999999");

        tourGuideCreateDTO = new TourGuideCreateDTO("CAD123456", TourGuideType.REGIONAL, userCreateDTO);
        tourGuideUpdateDTO = new TourGuideUpdateDTO("CAD654321", TourGuideType.REGIONAL, "Guide Updated", "999999999");
        tourGuideDTO = new TourGuideDTO(tourGuide.getTourGuideId(), "CAD123456", TourGuideType.REGIONAL, userOwner.getId(), "Guide", "999999999");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateTourGuide() {
        doNothing().when(userService).verifyUserAlreadyExists(tourGuideCreateDTO.user().email());
        when(tourGuideRepository.existsByCadastur(tourGuideCreateDTO.cadastur())).thenReturn(false);
        when(tourGuideMapper.toEntity(tourGuideCreateDTO)).thenReturn(tourGuide);
        when(bCryptPasswordEncoder.encode(any())).thenReturn("encodedPassword");
        when(tourGuideRepository.save(any(TourGuide.class))).thenReturn(tourGuide);
        when(tourGuideMapper.toDTO(tourGuide)).thenReturn(tourGuideDTO);

        TourGuideDTO result = tourGuideService.createTourGuide(tourGuideCreateDTO);

        assertNotNull(result);
        assertEquals(tourGuideDTO.tourGuideId(), result.tourGuideId());

        verify(userService, times(1)).verifyUserAlreadyExists(tourGuideCreateDTO.user().email());
        verify(tourGuideRepository, times(1)).existsByCadastur(tourGuideCreateDTO.cadastur());
        verify(tourGuideRepository, times(1)).save(any(TourGuide.class));
    }

    @Test
    void shouldGetTourGuide() {
        UUID guideId = tourGuide.getTourGuideId();

        when(tourGuideRepository.findById(guideId)).thenReturn(Optional.of(tourGuide));
        when(tourGuideMapper.toDTO(tourGuide)).thenReturn(tourGuideDTO);

        TourGuideDTO result = tourGuideService.getTourGuide(guideId);

        assertNotNull(result);
        assertEquals(guideId, result.tourGuideId());

        verify(tourGuideRepository, times(1)).findById(guideId);
    }

    @Test
    void shouldGetTourGuidesByTouristSpot() {
        UUID spotId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Page<TourGuide> guidePage = new PageImpl<>(List.of(tourGuide));

        when(tourGuideRepository.findAllByTouristSpots_TouristSpotId(spotId, pageable)).thenReturn(guidePage);
        when(tourGuideMapper.toDTO(tourGuide)).thenReturn(tourGuideDTO);

        Page<TourGuideDTO> result = tourGuideService.getTourGuidesByTouristSpot(spotId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(tourGuideRepository, times(1)).findAllByTouristSpots_TouristSpotId(spotId, pageable);
    }

    @Test
    void shouldUpdateTourGuide() {
        UUID guideId = tourGuide.getTourGuideId();
        UUID userId = userOwner.getId();

        when(tourGuideRepository.findById(guideId)).thenReturn(Optional.of(tourGuide));
        when(tourGuideRepository.existsByCadastur(tourGuideUpdateDTO.cadastur())).thenReturn(false);
        doNothing().when(tourGuideMapper).updateEntityFromDto(tourGuideUpdateDTO, tourGuide);
        when(tourGuideRepository.save(tourGuide)).thenReturn(tourGuide);
        when(tourGuideMapper.toDTO(tourGuide)).thenReturn(tourGuideDTO);

        TourGuideDTO result = tourGuideService.updateTourGuide(tourGuideUpdateDTO, guideId, userId);

        assertNotNull(result);

        verify(tourGuideRepository, times(1)).findById(guideId);
        verify(tourGuideRepository, times(1)).save(tourGuide);
    }

    @Test
    void shouldDeleteTourGuideWhenOwner() {
        UUID guideId = tourGuide.getTourGuideId();
        UUID userId = userOwner.getId();

        when(tourGuideRepository.findById(guideId)).thenReturn(Optional.of(tourGuide));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userOwner));
        doNothing().when(tourGuideRepository).delete(tourGuide);

        assertDoesNotThrow(() -> tourGuideService.deleteTourGuide(guideId, userId));

        verify(tourGuideRepository, times(1)).delete(tourGuide);
    }

    @Test
    void shouldDeleteTourGuideWhenAdmin() {
        UUID guideId = tourGuide.getTourGuideId();
        UUID adminId = adminUser.getId();

        when(tourGuideRepository.findById(guideId)).thenReturn(Optional.of(tourGuide));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        doNothing().when(tourGuideRepository).delete(tourGuide);

        assertDoesNotThrow(() -> tourGuideService.deleteTourGuide(guideId, adminId));

        verify(tourGuideRepository, times(1)).delete(tourGuide);
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateTourGuideAndThrowExceptionWhenCadasturExists() {
        doNothing().when(userService).verifyUserAlreadyExists(tourGuideCreateDTO.user().email());
        when(tourGuideRepository.existsByCadastur(tourGuideCreateDTO.cadastur())).thenReturn(true);

        assertThrows(
                ObjectAlreadyExistsException.class,
                () -> tourGuideService.createTourGuide(tourGuideCreateDTO)
        );

        verify(tourGuideRepository, times(1)).existsByCadastur(tourGuideCreateDTO.cadastur());
        verify(tourGuideRepository, never()).save(any());
    }

    @Test
    void shouldNotGetTourGuideAndThrowExceptionWhenNotFound() {
        UUID guideId = UUID.randomUUID();

        when(tourGuideRepository.findById(guideId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> tourGuideService.getTourGuide(guideId)
        );

        verify(tourGuideRepository, times(1)).findById(guideId);
    }

    @Test
    void shouldNotUpdateTourGuideAndThrowExceptionWhenNotOwner() {
        UUID guideId = tourGuide.getTourGuideId();
        UUID otherUserId = otherUser.getId();

        when(tourGuideRepository.findById(guideId)).thenReturn(Optional.of(tourGuide));

        assertThrows(
                UserIsNotOwnerException.class,
                () -> tourGuideService.updateTourGuide(tourGuideUpdateDTO, guideId, otherUserId)
        );

        verify(tourGuideRepository, never()).save(any());
    }

    @Test
    void shouldNotUpdateTourGuideAndThrowExceptionWhenCadasturAlreadyExists() {
        UUID guideId = tourGuide.getTourGuideId();
        UUID userId = userOwner.getId();

        when(tourGuideRepository.findById(guideId)).thenReturn(Optional.of(tourGuide));
        when(tourGuideRepository.existsByCadastur(tourGuideUpdateDTO.cadastur())).thenReturn(true);

        assertThrows(
                ObjectAlreadyExistsException.class,
                () -> tourGuideService.updateTourGuide(tourGuideUpdateDTO, guideId, userId)
        );

        verify(tourGuideRepository, never()).save(any());
    }

    @Test
    void shouldNotDeleteTourGuideAndThrowExceptionWhenNotAdminNorOwner() {
        UUID guideId = tourGuide.getTourGuideId();
        UUID otherUserId = otherUser.getId();

        when(tourGuideRepository.findById(guideId)).thenReturn(Optional.of(tourGuide));
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));

        assertThrows(
                UserIsNotAdminOrOwnerException.class,
                () -> tourGuideService.deleteTourGuide(guideId, otherUserId)
        );

        verify(tourGuideRepository, never()).delete(any());
    }
}
