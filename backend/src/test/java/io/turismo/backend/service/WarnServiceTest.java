package io.turismo.backend.service;

import io.turismo.backend.dto.warn.WarnCreateDTO;
import io.turismo.backend.dto.warn.WarnDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.mapper.WarnMapper;
import io.turismo.backend.model.SpotManager;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.model.User;
import io.turismo.backend.model.Warn;
import io.turismo.backend.repository.TouristSpotRepository;
import io.turismo.backend.repository.WarnRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarnServiceTest {

    @Mock
    private WarnRepository warnRepository;
    @Mock
    private WarnMapper warnMapper;
    @Mock
    private TouristSpotRepository touristSpotRepository;

    @InjectMocks
    private WarnService warnService;

    private User userOwner;
    private User userNotOwner;
    private SpotManager spotManager;
    private TouristSpot touristSpot;
    private Warn warn;
    private WarnDTO warnDTO;
    private WarnCreateDTO warnCreateDTO;

    @BeforeEach
    void setUp() {
        userOwner = new User();
        ReflectionTestUtils.setField(userOwner, "id", UUID.randomUUID());

        userNotOwner = new User();
        ReflectionTestUtils.setField(userNotOwner, "id", UUID.randomUUID());

        spotManager = new SpotManager();
        ReflectionTestUtils.setField(spotManager, "spotManagerId", UUID.randomUUID());
        spotManager.setUser(userOwner);

        touristSpot = new TouristSpot();
        ReflectionTestUtils.setField(touristSpot, "touristSpotId", UUID.randomUUID());
        touristSpot.setSpotManager(spotManager);

        warn = new Warn();
        ReflectionTestUtils.setField(warn, "id", UUID.randomUUID());
        warn.setName("Aviso de Manutenção");
        warn.setDescription("O local estará fechado.");
        warn.setEventDate(LocalDate.now());
        warn.setTouristSpot(touristSpot);

        warnDTO = new WarnDTO(warn.getId(), warn.getName(), warn.getDescription(), warn.getEventDate());
        warnCreateDTO = new WarnCreateDTO("Aviso de Manutenção", "O local estará fechado.");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateWarn() {
        UUID ownerId = userOwner.getId();
        UUID spotId = touristSpot.getTouristSpotId();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.of(touristSpot));
        when(warnMapper.toEntity(warnCreateDTO)).thenReturn(warn);
        when(warnRepository.save(any(Warn.class))).thenReturn(warn);
        when(warnMapper.toDTO(warn)).thenReturn(warnDTO);

        WarnDTO result = warnService.createWarn(ownerId, warnCreateDTO, spotId);

        assertNotNull(result);
        assertEquals("Aviso de Manutenção", result.name());

        verify(touristSpotRepository, times(1)).findById(spotId);
        verify(warnRepository, times(1)).save(any(Warn.class));
    }

    @Test
    void shouldGetWarn() {
        UUID warnId = warn.getId();

        when(warnRepository.findById(warnId)).thenReturn(Optional.of(warn));
        when(warnMapper.toDTO(warn)).thenReturn(warnDTO);

        WarnDTO result = warnService.getWarn(warnId);

        assertNotNull(result);
        assertEquals("Aviso de Manutenção", result.name());

        verify(warnRepository, times(1)).findById(warnId);
    }

    @Test
    void shouldGetAllTouristSpotWarn() {
        UUID spotId = touristSpot.getTouristSpotId();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Warn> warnPage = new PageImpl<>(List.of(warn));

        when(warnRepository.findAllByTouristSpot_TouristSpotId(spotId, pageable)).thenReturn(warnPage);
        when(warnMapper.toDTO(warn)).thenReturn(warnDTO);

        Page<WarnDTO> result = warnService.getAllTouristSpotWarn(spotId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(warnRepository, times(1)).findAllByTouristSpot_TouristSpotId(spotId, pageable);
    }

    @Test
    void shouldDeleteWarn() {
        UUID ownerId = userOwner.getId();
        UUID warnId = warn.getId();

        when(warnRepository.findById(warnId)).thenReturn(Optional.of(warn));
        doNothing().when(warnRepository).delete(warn);

        assertDoesNotThrow(() -> warnService.deleteWarn(ownerId, warnId));

        verify(warnRepository, times(1)).findById(warnId);
        verify(warnRepository, times(1)).delete(warn);
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateWarnAndThrowExceptionWhenTouristSpotNotFound() {
        UUID ownerId = userOwner.getId();
        UUID spotId = UUID.randomUUID();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> warnService.createWarn(ownerId, warnCreateDTO, spotId)
        );

        verify(touristSpotRepository, times(1)).findById(spotId);
        verify(warnRepository, never()).save(any(Warn.class));
    }

    @Test
    void shouldNotCreateWarnAndThrowExceptionWhenUserIsNotOwner() {
        UUID notOwnerId = userNotOwner.getId();
        UUID spotId = touristSpot.getTouristSpotId();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.of(touristSpot));

        assertThrows(
                UserIsNotOwnerException.class,
                () -> warnService.createWarn(notOwnerId, warnCreateDTO, spotId)
        );

        verify(touristSpotRepository, times(1)).findById(spotId);
        verify(warnRepository, never()).save(any(Warn.class));
    }

    @Test
    void shouldNotGetWarnAndThrowExceptionWhenWarnNotFound() {
        UUID warnId = UUID.randomUUID();

        when(warnRepository.findById(warnId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> warnService.getWarn(warnId)
        );

        verify(warnRepository, times(1)).findById(warnId);
    }

    @Test
    void shouldNotDeleteWarnAndThrowExceptionWhenWarnNotFound() {
        UUID ownerId = userOwner.getId();
        UUID warnId = UUID.randomUUID();

        when(warnRepository.findById(warnId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> warnService.deleteWarn(ownerId, warnId)
        );

        verify(warnRepository, times(1)).findById(warnId);
        verify(warnRepository, never()).delete(any(Warn.class));
    }

    @Test
    void shouldNotDeleteWarnAndThrowExceptionWhenUserIsNotOwner() {
        UUID notOwnerId = userNotOwner.getId();
        UUID warnId = warn.getId();

        when(warnRepository.findById(warnId)).thenReturn(Optional.of(warn));

        assertThrows(
                UserIsNotOwnerException.class,
                () -> warnService.deleteWarn(notOwnerId, warnId)
        );

        verify(warnRepository, times(1)).findById(warnId);
        verify(warnRepository, never()).delete(any(Warn.class));
    }
}
