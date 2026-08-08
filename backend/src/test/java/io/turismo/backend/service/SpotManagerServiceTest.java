package io.turismo.backend.service;

import io.turismo.backend.dto.spot_manager.SpotManagerCreateDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerSimpleDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerUpdateDTO;
import io.turismo.backend.dto.user.UserCreateDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.mapper.SpotManagerMapper;
import io.turismo.backend.model.SpotManager;
import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.ManagerType;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.SpotManagerRepository;
import io.turismo.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpotManagerServiceTest {

    @Mock
    private SpotManagerRepository spotManagerRepository;
    @Mock
    private SpotManagerMapper spotManagerMapper;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private SpotManagerService spotManagerService;

    private User userOwner;
    private User adminUser;
    private User otherUser;
    private SpotManager spotManager;
    private SpotManagerSimpleDTO spotManagerSimpleDTO;
    private SpotManagerDTO spotManagerDTO;
    private SpotManagerCreateDTO spotManagerCreateDTO;
    private SpotManagerUpdateDTO spotManagerUpdateDTO;

    @BeforeEach
    void setUp() {
        userOwner = new User();
        ReflectionTestUtils.setField(userOwner, "id", UUID.randomUUID());
        userOwner.setEmail("manager@gmail.com");
        userOwner.setName("Manager");
        userOwner.setPhone("999999999");
        userOwner.setRole(UserRole.SPOTMANAGER);

        adminUser = new User();
        ReflectionTestUtils.setField(adminUser, "id", UUID.randomUUID());
        adminUser.setRole(UserRole.ADMIN);

        otherUser = new User();
        ReflectionTestUtils.setField(otherUser, "id", UUID.randomUUID());
        otherUser.setRole(UserRole.SPOTMANAGER);

        spotManager = new SpotManager();
        ReflectionTestUtils.setField(spotManager, "spotManagerId", UUID.randomUUID());
        spotManager.setUser(userOwner);
        spotManager.setManagerType(ManagerType.PRIVATE);

        UserCreateDTO userCreateDTO = new UserCreateDTO("Manager", "manager@gmail.com", "pass123", "999999999");

        spotManagerCreateDTO = new SpotManagerCreateDTO(ManagerType.PRIVATE, userCreateDTO);
        spotManagerUpdateDTO = new SpotManagerUpdateDTO("Manager Updated", "999999999", ManagerType.PRIVATE);
        spotManagerSimpleDTO = new SpotManagerSimpleDTO(spotManager.getSpotManagerId(), ManagerType.PRIVATE, "Manager", "999999999");
        spotManagerDTO = new SpotManagerDTO(spotManager.getSpotManagerId(), userOwner.getId(), ManagerType.PRIVATE, "Manager", "999999999");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateSpotManager() {
        doNothing().when(userService).verifyUserAlreadyExists(spotManagerCreateDTO.user().email());
        when(spotManagerMapper.toEntity(spotManagerCreateDTO)).thenReturn(spotManager);
        when(bCryptPasswordEncoder.encode(any())).thenReturn("encodedPassword");
        when(spotManagerRepository.save(any(SpotManager.class))).thenReturn(spotManager);
        when(spotManagerMapper.toSimpleDTO(spotManager)).thenReturn(spotManagerSimpleDTO);

        SpotManagerSimpleDTO result = spotManagerService.createSpotManager(spotManagerCreateDTO);

        assertNotNull(result);
        assertEquals(spotManagerSimpleDTO.spotManagerId(), result.spotManagerId());

        verify(userService, times(1)).verifyUserAlreadyExists(spotManagerCreateDTO.user().email());
        verify(spotManagerRepository, times(1)).save(any(SpotManager.class));
    }

    @Test
    void shouldGetSpotManager() {
        UUID managerId = spotManager.getSpotManagerId();

        when(spotManagerRepository.findById(managerId)).thenReturn(Optional.of(spotManager));
        when(spotManagerMapper.toSimpleDTO(spotManager)).thenReturn(spotManagerSimpleDTO);

        SpotManagerSimpleDTO result = spotManagerService.getSpotManager(managerId);

        assertNotNull(result);
        assertEquals(managerId, result.spotManagerId());

        verify(spotManagerRepository, times(1)).findById(managerId);
    }

    @Test
    void shouldCurrentSpotManager() {
        UUID managerId = spotManager.getSpotManagerId();

        when(spotManagerRepository.findById(managerId)).thenReturn(Optional.of(spotManager));
        when(spotManagerMapper.toDTO(spotManager)).thenReturn(spotManagerDTO);

        SpotManagerDTO result = spotManagerService.currentSpotManager(managerId);

        assertNotNull(result);
        assertEquals(managerId, result.spotManagerId());

        verify(spotManagerRepository, times(1)).findById(managerId);
    }

    @Test
    void shouldUpdateSpotManager() {
        UUID managerId = spotManager.getSpotManagerId();
        UUID userId = userOwner.getId();

        when(spotManagerRepository.findById(managerId)).thenReturn(Optional.of(spotManager));
        doNothing().when(spotManagerMapper).updateEntityFromDto(spotManagerUpdateDTO, spotManager);
        when(spotManagerRepository.save(spotManager)).thenReturn(spotManager);
        when(spotManagerMapper.toSimpleDTO(spotManager)).thenReturn(spotManagerSimpleDTO);

        SpotManagerSimpleDTO result = spotManagerService.updateSpotManager(spotManagerUpdateDTO, managerId, userId);

        assertNotNull(result);

        verify(spotManagerRepository, times(1)).findById(managerId);
        verify(spotManagerRepository, times(1)).save(spotManager);
    }

    @Test
    void shouldDeleteSpotManagerWhenOwner() {
        UUID managerId = spotManager.getSpotManagerId();
        UUID userId = userOwner.getId();

        when(spotManagerRepository.findById(managerId)).thenReturn(Optional.of(spotManager));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userOwner));
        doNothing().when(spotManagerRepository).delete(spotManager);

        assertDoesNotThrow(() -> spotManagerService.deleteSpotManager(managerId, userId));

        verify(spotManagerRepository, times(1)).delete(spotManager);
    }

    @Test
    void shouldDeleteSpotManagerWhenAdmin() {
        UUID managerId = spotManager.getSpotManagerId();
        UUID adminId = adminUser.getId();

        when(spotManagerRepository.findById(managerId)).thenReturn(Optional.of(spotManager));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        doNothing().when(spotManagerRepository).delete(spotManager);

        assertDoesNotThrow(() -> spotManagerService.deleteSpotManager(managerId, adminId));

        verify(spotManagerRepository, times(1)).delete(spotManager);
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotGetSpotManagerAndThrowExceptionWhenNotFound() {
        UUID managerId = UUID.randomUUID();

        when(spotManagerRepository.findById(managerId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> spotManagerService.getSpotManager(managerId)
        );

        verify(spotManagerRepository, times(1)).findById(managerId);
    }

    @Test
    void shouldNotCurrentSpotManagerAndThrowExceptionWhenNotFound() {
        UUID managerId = UUID.randomUUID();

        when(spotManagerRepository.findById(managerId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> spotManagerService.currentSpotManager(managerId)
        );

        verify(spotManagerRepository, times(1)).findById(managerId);
    }

    @Test
    void shouldNotUpdateSpotManagerAndThrowExceptionWhenNotOwner() {
        UUID managerId = spotManager.getSpotManagerId();
        UUID otherUserId = otherUser.getId();

        when(spotManagerRepository.findById(managerId)).thenReturn(Optional.of(spotManager));

        assertThrows(
                UserIsNotOwnerException.class,
                () -> spotManagerService.updateSpotManager(spotManagerUpdateDTO, managerId, otherUserId)
        );

        verify(spotManagerRepository, never()).save(any());
    }

    @Test
    void shouldNotDeleteSpotManagerAndThrowExceptionWhenNotAdminNorOwner() {
        UUID managerId = spotManager.getSpotManagerId();
        UUID otherUserId = otherUser.getId();

        when(spotManagerRepository.findById(managerId)).thenReturn(Optional.of(spotManager));
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));

        assertThrows(
                UserIsNotAdminOrOwnerException.class,
                () -> spotManagerService.deleteSpotManager(managerId, otherUserId)
        );

        verify(spotManagerRepository, never()).delete(any());
    }
}
