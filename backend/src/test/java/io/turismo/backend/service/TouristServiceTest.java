package io.turismo.backend.service;

import io.turismo.backend.dto.tourist.TouristCreateDTO;
import io.turismo.backend.dto.tourist.TouristDTO;
import io.turismo.backend.dto.tourist.TouristUpdateDTO;
import io.turismo.backend.dto.user.UserCreateDTO;
import io.turismo.backend.exception.InvalidDateException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.mapper.TouristMapper;
import io.turismo.backend.model.Tourist;
import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.TouristRepository;
import io.turismo.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TouristServiceTest {

    @Mock
    private TouristRepository touristRepository;
    @Mock
    private TouristMapper touristMapper;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private TouristService touristService;

    private User userPerson1;
    private User adminUser;
    private User otherUser;
    private Tourist tourist1;
    private TouristDTO touristDTO;
    private TouristCreateDTO touristCreateDTO;
    private TouristUpdateDTO touristUpdateDTO;

    @BeforeEach
    void setUp() {
        userPerson1 = new User();
        ReflectionTestUtils.setField(userPerson1, "id", UUID.randomUUID());
        userPerson1.setEmail("tourist@gmail.com");
        userPerson1.setName("John Doe");
        userPerson1.setPhone("123456789");
        userPerson1.setRole(UserRole.TOURIST);

        adminUser = new User();
        ReflectionTestUtils.setField(adminUser, "id", UUID.randomUUID());
        adminUser.setRole(UserRole.ADMIN);

        otherUser = new User();
        ReflectionTestUtils.setField(otherUser, "id", UUID.randomUUID());
        otherUser.setRole(UserRole.TOURIST);

        tourist1 = new Tourist();
        ReflectionTestUtils.setField(tourist1, "touristId", UUID.randomUUID());
        tourist1.setUser(userPerson1);
        tourist1.setBirthDate(LocalDate.now().minusYears(25));

        UserCreateDTO userCreateDTO = new UserCreateDTO("John Doe", "tourist@gmail.com", "password123", "123456789");

        touristCreateDTO = new TouristCreateDTO(LocalDate.now().minusYears(25), userCreateDTO);
        touristUpdateDTO = new TouristUpdateDTO(LocalDate.now().minusYears(26), "John Doe Updated", "123456789", Collections.emptySet());
        touristDTO = new TouristDTO(tourist1.getTouristId(), tourist1.getBirthDate(), userPerson1.getId(), "John Doe", "123456789");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateTourist() {
        doNothing().when(userService).verifyUserAlreadyExists(touristCreateDTO.user().email());
        when(touristMapper.toEntity(touristCreateDTO)).thenReturn(tourist1);
        when(bCryptPasswordEncoder.encode(any())).thenReturn("encodedPassword");
        when(touristRepository.save(any(Tourist.class))).thenReturn(tourist1);
        when(touristMapper.toDTO(tourist1)).thenReturn(touristDTO);

        TouristDTO result = touristService.createTourist(touristCreateDTO);

        assertNotNull(result);
        assertEquals(touristDTO.touristId(), result.touristId());

        verify(userService, times(1)).verifyUserAlreadyExists(touristCreateDTO.user().email());
        verify(touristRepository, times(1)).save(any(Tourist.class));
    }

    @Test
    void shouldGetTourist() {
        UUID touristId = tourist1.getTouristId();

        when(touristRepository.findById(touristId)).thenReturn(Optional.of(tourist1));
        when(touristMapper.toDTO(tourist1)).thenReturn(touristDTO);

        TouristDTO result = touristService.getTourist(touristId);

        assertNotNull(result);
        assertEquals(touristId, result.touristId());

        verify(touristRepository, times(1)).findById(touristId);
    }

    @Test
    void shouldUpdateTourist() {
        UUID touristId = tourist1.getTouristId();
        UUID userId = userPerson1.getId();

        when(touristRepository.findById(touristId)).thenReturn(Optional.of(tourist1));
        doNothing().when(touristMapper).updateEntityFromDTO(touristUpdateDTO, tourist1);
        when(touristRepository.save(tourist1)).thenReturn(tourist1);
        when(touristMapper.toDTO(tourist1)).thenReturn(touristDTO);

        TouristDTO result = touristService.updateTourist(touristUpdateDTO, touristId, userId);

        assertNotNull(result);

        verify(touristRepository, times(1)).findById(touristId);
        verify(touristRepository, times(1)).save(tourist1);
    }

    @Test
    void shouldDeleteTouristWhenOwner() {
        UUID touristId = tourist1.getTouristId();
        UUID userId = userPerson1.getId();

        when(touristRepository.findById(touristId)).thenReturn(Optional.of(tourist1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userPerson1));
        doNothing().when(touristRepository).delete(tourist1);

        assertDoesNotThrow(() -> touristService.deleteTourist(touristId, userId));

        verify(touristRepository, times(1)).delete(tourist1);
    }

    @Test
    void shouldDeleteTouristWhenAdmin() {
        UUID touristId = tourist1.getTouristId();
        UUID adminId = adminUser.getId();

        when(touristRepository.findById(touristId)).thenReturn(Optional.of(tourist1));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        doNothing().when(touristRepository).delete(tourist1);

        assertDoesNotThrow(() -> touristService.deleteTourist(touristId, adminId));

        verify(touristRepository, times(1)).delete(tourist1);
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateTouristAndThrowExceptionWhenInvalidAge() {
        UserCreateDTO userCreateDTO = new UserCreateDTO("John Doe", "tourist@gmail.com", "password123", "123456789");
        TouristCreateDTO invalidAgeDTO = new TouristCreateDTO(LocalDate.now().minusYears(130), userCreateDTO);

        doNothing().when(userService).verifyUserAlreadyExists(invalidAgeDTO.user().email());

        assertThrows(
                InvalidDateException.class,
                () -> touristService.createTourist(invalidAgeDTO)
        );

        verify(touristRepository, never()).save(any(Tourist.class));
    }

    @Test
    void shouldNotGetTouristAndThrowExceptionWhenNotFound() {
        UUID touristId = UUID.randomUUID();

        when(touristRepository.findById(touristId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> touristService.getTourist(touristId)
        );

        verify(touristRepository, times(1)).findById(touristId);
    }

    @Test
    void shouldNotUpdateTouristAndThrowExceptionWhenNotOwner() {
        UUID touristId = tourist1.getTouristId();
        UUID otherUserId = otherUser.getId();

        when(touristRepository.findById(touristId)).thenReturn(Optional.of(tourist1));

        assertThrows(
                UserIsNotOwnerException.class,
                () -> touristService.updateTourist(touristUpdateDTO, touristId, otherUserId)
        );

        verify(touristRepository, never()).save(any());
    }

    @Test
    void shouldNotDeleteTouristAndThrowExceptionWhenNotAdminNorOwner() {
        UUID touristId = tourist1.getTouristId();
        UUID otherUserId = otherUser.getId();

        when(touristRepository.findById(touristId)).thenReturn(Optional.of(tourist1));
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));

        assertThrows(
                UserIsNotAdminOrOwnerException.class,
                () -> touristService.deleteTourist(touristId, otherUserId)
        );

        verify(touristRepository, never()).delete(any());
    }
}
