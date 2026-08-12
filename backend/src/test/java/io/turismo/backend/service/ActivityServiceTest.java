package io.turismo.backend.service;

import io.turismo.backend.dto.activity.ActivityCreateDTO;
import io.turismo.backend.dto.activity.ActivityDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.mapper.ActivityMapper;
import io.turismo.backend.model.Activity;
import io.turismo.backend.model.SpotManager;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.ActivityRepository;
import io.turismo.backend.repository.TouristSpotRepository;
import io.turismo.backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ActivityMapper activityMapper;

    @Mock
    private TouristSpotRepository touristSpotRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ActivityService activityService;

    private UUID userId;
    private UUID otherUserId;
    private UUID adminUserId;
    private UUID touristSpotId;
    private UUID activityId;

    private User ownerUser;
    private User otherUser;
    private User adminUser;
    private SpotManager spotManager;
    private TouristSpot touristSpot;
    private Activity activity;
    private ActivityDTO activityDTO;
    private ActivityCreateDTO activityCreateDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        adminUserId = UUID.randomUUID();
        touristSpotId = UUID.randomUUID();
        activityId = UUID.randomUUID();

        ownerUser = new User();
        ReflectionTestUtils.setField(ownerUser, "id", userId);
        ownerUser.setRole(UserRole.SPOTMANAGER);

        otherUser = new User();
        ReflectionTestUtils.setField(otherUser, "id", otherUserId);
        otherUser.setRole(UserRole.TOURIST);

        adminUser = new User();
        ReflectionTestUtils.setField(adminUser, "id", adminUserId);
        adminUser.setRole(UserRole.ADMIN);

        spotManager = new SpotManager();
        spotManager.setUser(ownerUser);

        touristSpot = new TouristSpot();
        ReflectionTestUtils.setField(touristSpot, "touristSpotId", touristSpotId);
        touristSpot.setSpotManager(spotManager);

        activity = new Activity();
        ReflectionTestUtils.setField(activity, "activityId", activityId);
        activity.setName("Trilha Ecológica");
        activity.setTouristSpot(touristSpot);

        activityDTO = new ActivityDTO(activityId, "Trilha Ecológica", null);
        activityCreateDTO = new ActivityCreateDTO("Trilha Ecológica");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateActivity() {
        when(touristSpotRepository.findById(touristSpotId)).thenReturn(Optional.of(touristSpot));
        when(activityMapper.toEntity(activityCreateDTO)).thenReturn(activity);
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);
        when(activityMapper.toDTO(activity)).thenReturn(activityDTO);

        ActivityDTO result = activityService.createActivity(touristSpotId, userId, activityCreateDTO);

        assertNotNull(result);
        assertEquals(activityDTO.name(), result.name());

        verify(touristSpotRepository, times(1)).findById(touristSpotId);
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void shouldGetActivitiesByTouristSpotId() {
        when(touristSpotRepository.existsById(touristSpotId)).thenReturn(true);
        when(activityRepository.findAllByTouristSpot_TouristSpotId(touristSpotId)).thenReturn(Set.of(activity));
        when(activityMapper.toDTO(activity)).thenReturn(activityDTO);

        Set<ActivityDTO> result = activityService.getActivitiesByTouristSpotId(touristSpotId);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(touristSpotRepository, times(1)).existsById(touristSpotId);
        verify(activityRepository, times(1)).findAllByTouristSpot_TouristSpotId(touristSpotId);
    }

    @Test
    void shouldUpdateActivity() {
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        doNothing().when(activityMapper).updateEntityFromDTO(activityCreateDTO, activity);
        when(activityRepository.save(activity)).thenReturn(activity);

        assertDoesNotThrow(() -> activityService.updateActivity(activityId, userId, activityCreateDTO));

        verify(activityRepository, times(1)).findById(activityId);
        verify(activityRepository, times(1)).save(activity);
    }

    @Test
    void shouldDeleteActivityWhenUserIsOwner() {
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(userRepository.findById(userId)).thenReturn(Optional.of(ownerUser));
        doNothing().when(activityRepository).delete(activity);

        assertDoesNotThrow(() -> activityService.deleteActivity(activityId, userId));

        verify(activityRepository, times(1)).findById(activityId);
        verify(activityRepository, times(1)).delete(activity);
    }

    @Test
    void shouldDeleteActivityWhenUserIsAdmin() {
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
        doNothing().when(activityRepository).delete(activity);

        assertDoesNotThrow(() -> activityService.deleteActivity(activityId, adminUserId));

        verify(activityRepository, times(1)).findById(activityId);
        verify(activityRepository, times(1)).delete(activity);
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateActivityAndThrowExceptionWhenTouristSpotNotFound() {
        when(touristSpotRepository.findById(touristSpotId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> activityService.createActivity(touristSpotId, userId, activityCreateDTO)
        );

        verify(touristSpotRepository, times(1)).findById(touristSpotId);
        verify(activityRepository, never()).save(any(Activity.class));
    }

    @Test
    void shouldNotCreateActivityAndThrowExceptionWhenUserIsNotOwner() {
        when(touristSpotRepository.findById(touristSpotId)).thenReturn(Optional.of(touristSpot));

        assertThrows(
                UserIsNotOwnerException.class,
                () -> activityService.createActivity(touristSpotId, otherUserId, activityCreateDTO)
        );

        verify(touristSpotRepository, times(1)).findById(touristSpotId);
        verify(activityRepository, never()).save(any(Activity.class));
    }

    @Test
    void shouldNotGetActivitiesByTouristSpotIdAndThrowExceptionWhenTouristSpotNotFound() {
        when(touristSpotRepository.existsById(touristSpotId)).thenReturn(false);

        assertThrows(
                ObjectNotFoundException.class,
                () -> activityService.getActivitiesByTouristSpotId(touristSpotId)
        );

        verify(touristSpotRepository, times(1)).existsById(touristSpotId);
        verify(activityRepository, never()).findAllByTouristSpot_TouristSpotId(any());
    }

    @Test
    void shouldNotUpdateActivityAndThrowExceptionWhenActivityNotFound() {
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> activityService.updateActivity(activityId, userId, activityCreateDTO)
        );

        verify(activityRepository, times(1)).findById(activityId);
        verify(activityRepository, never()).save(any());
    }

    @Test
    void shouldNotUpdateActivityAndThrowExceptionWhenUserIsNotOwner() {
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

        assertThrows(
                UserIsNotOwnerException.class,
                () -> activityService.updateActivity(activityId, otherUserId, activityCreateDTO)
        );

        verify(activityRepository, times(1)).findById(activityId);
        verify(activityRepository, never()).save(any());
    }

    @Test
    void shouldNotDeleteActivityAndThrowExceptionWhenActivityNotFound() {
        when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> activityService.deleteActivity(activityId, userId)
        );

        verify(activityRepository, times(1)).findById(activityId);
        verify(activityRepository, never()).delete(any());
    }

    @Test
    void shouldNotDeleteActivityAndThrowExceptionWhenUserNotFound() {
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> activityService.deleteActivity(activityId, userId)
        );

        verify(activityRepository, times(1)).findById(activityId);
        verify(userRepository, times(1)).findById(userId);
        verify(activityRepository, never()).delete(any());
    }

    @Test
    void shouldNotDeleteActivityAndThrowExceptionWhenUserIsNotAdminOrOwner() {
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));

        assertThrows(
                UserIsNotAdminOrOwnerException.class,
                () -> activityService.deleteActivity(activityId, otherUserId)
        );

        verify(activityRepository, times(1)).findById(activityId);
        verify(userRepository, times(1)).findById(otherUserId);
        verify(activityRepository, never()).delete(any());
    }
}
