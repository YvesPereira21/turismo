package io.turismo.backend.service;

import io.turismo.backend.dto.geojson.GeoFeatureCollectionDTO;
import io.turismo.backend.dto.tourist_spot.*;
import io.turismo.backend.exception.InvalidDateException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.mapper.TouristSpotMapper;
import io.turismo.backend.model.City;
import io.turismo.backend.model.SpotManager;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.CityRepository;
import io.turismo.backend.repository.SpotManagerRepository;
import io.turismo.backend.repository.TouristSpotRepository;
import io.turismo.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TouristSpotServiceTest {

    @Mock
    private TouristSpotRepository touristSpotRepository;
    @Mock
    private TouristSpotMapper touristSpotMapper;
    @Mock
    private SpotManagerRepository spotManagerRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private TagService tagService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TouristSpotService touristSpotService;

    private User userOwner;
    private User adminUser;
    private User otherUser;
    private SpotManager spotManager;
    private City city;
    private TouristSpot touristSpot;
    private TouristSpotDTO touristSpotDTO;
    private TouristSpotListDTO touristSpotListDTO;
    private TouristSpotCreateDTO touristSpotCreateDTO;
    private TouristSpotUpdateDTO touristSpotUpdateDTO;

    @BeforeEach
    void setUp() {
        userOwner = new User();
        ReflectionTestUtils.setField(userOwner, "id", UUID.randomUUID());
        userOwner.setEmail("manager@gmail.com");
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

        city = new City();
        ReflectionTestUtils.setField(city, "cityId", UUID.randomUUID());
        city.setName("São Paulo");

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point location = geometryFactory.createPoint(new Coordinate(-46.633308, -23.550520));

        touristSpot = new TouristSpot();
        ReflectionTestUtils.setField(touristSpot, "touristSpotId", UUID.randomUUID());
        touristSpot.setName("Parque Ibirapuera");
        touristSpot.setSpotManager(spotManager);
        touristSpot.setCity(city);
        touristSpot.setLocation(location);
        touristSpot.setOpensAt(LocalTime.of(8, 0));
        touristSpot.setClosesAt(LocalTime.of(18, 0));

        touristSpotCreateDTO = new TouristSpotCreateDTO(
                "Parque Ibirapuera", -23.550520, -46.633308,
                LocalTime.of(8, 0), LocalTime.of(18, 0), "Descrição curta", "Descrição",
                city.getCityId(), Set.of("Parque"), Collections.emptyList()
        );

        touristSpotUpdateDTO = new TouristSpotUpdateDTO(
                "Parque Ibirapuera Modificado", -23.550520, -46.633308,
                LocalTime.of(8, 0), LocalTime.of(18, 0), "Descrição curta", "Descrição",
                city.getCityId(), Set.of("Parque"), Collections.emptyList()
        );

        touristSpotDTO = new TouristSpotDTO(
                touristSpot.getTouristSpotId(), "Parque Ibirapuera", location, LocalTime.of(8, 0), LocalTime.of(18, 0),
                "Descrição curta", "Descrição", null, null, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
        );

        touristSpotListDTO = new TouristSpotListDTO(
                touristSpot.getTouristSpotId(), "Parque Ibirapuera", LocalTime.of(8, 0), LocalTime.of(18, 0),
                "Descrição curta", null, Collections.emptyList(), Collections.emptyList(), null
        );
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateTouristSpot() {
        UUID userId = userOwner.getId();

        when(spotManagerRepository.findByUser_Id(userId)).thenReturn(Optional.of(spotManager));
        when(cityRepository.findById(touristSpotCreateDTO.cityId())).thenReturn(Optional.of(city));
        when(touristSpotMapper.toEntity(touristSpotCreateDTO)).thenReturn(touristSpot);
        when(tagService.convertNamesToTags(touristSpotCreateDTO.tags())).thenReturn(Collections.emptySet());
        when(touristSpotRepository.save(any(TouristSpot.class))).thenReturn(touristSpot);
        when(touristSpotMapper.toDTO(touristSpot)).thenReturn(touristSpotDTO);

        TouristSpotDTO result = touristSpotService.createTouristSpot(touristSpotCreateDTO, userId);

        assertNotNull(result);
        assertEquals("Parque Ibirapuera", result.name());

        verify(spotManagerRepository, times(1)).findByUser_Id(userId);
        verify(cityRepository, times(1)).findById(touristSpotCreateDTO.cityId());
        verify(touristSpotRepository, times(1)).save(any(TouristSpot.class));
    }

    @Test
    void shouldGetTouristSpot() {
        UUID spotId = touristSpot.getTouristSpotId();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.of(touristSpot));
        when(touristSpotMapper.toDTO(touristSpot)).thenReturn(touristSpotDTO);

        TouristSpotDTO result = touristSpotService.getTouristSpot(spotId);

        assertNotNull(result);
        assertEquals(spotId, result.touristSpotId());

        verify(touristSpotRepository, times(1)).findById(spotId);
    }

    @Test
    void shouldGetTouristSpotsToMap() {
        when(touristSpotRepository.findAll()).thenReturn(List.of(touristSpot));

        GeoFeatureCollectionDTO<TouristSpotToMapDTO> result = touristSpotService.getTouristSpotsToMap();

        assertNotNull(result);
        assertEquals(1, result.features().size());

        verify(touristSpotRepository, times(1)).findAll();
    }

    @Test
    void shouldGetTouristSpots() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TouristSpot> page = new PageImpl<>(List.of(touristSpot));

        when(touristSpotRepository.findAll(pageable)).thenReturn(page);
        when(touristSpotMapper.toListDTO(touristSpot)).thenReturn(touristSpotListDTO);

        Page<TouristSpotListDTO> result = touristSpotService.getTouristSpots(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(touristSpotRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldGetTouristSpotsFromState() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TouristSpot> page = new PageImpl<>(List.of(touristSpot));

        when(touristSpotRepository.findAllByStateName("SP", pageable)).thenReturn(page);
        when(touristSpotMapper.toListDTO(touristSpot)).thenReturn(touristSpotListDTO);

        Page<TouristSpotListDTO> result = touristSpotService.getTouristSpotsFromState("SP", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(touristSpotRepository, times(1)).findAllByStateName("SP", pageable);
    }

    @Test
    void shouldGetSpotManagerTouristSpots() {
        UUID managerId = spotManager.getSpotManagerId();
        Pageable pageable = PageRequest.of(0, 10);
        Page<TouristSpot> page = new PageImpl<>(List.of(touristSpot));

        when(touristSpotRepository.findAllBySpotManager_SpotManagerId(managerId, pageable)).thenReturn(page);
        when(touristSpotMapper.toListDTO(touristSpot)).thenReturn(touristSpotListDTO);

        Page<TouristSpotListDTO> result = touristSpotService.getSpotManagerTouristSpots(managerId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(touristSpotRepository, times(1)).findAllBySpotManager_SpotManagerId(managerId, pageable);
    }

    @Test
    void shouldUpdateTouristSpot() {
        UUID spotId = touristSpot.getTouristSpotId();
        UUID userId = userOwner.getId();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.of(touristSpot));
        when(cityRepository.findById(touristSpotUpdateDTO.cityId())).thenReturn(Optional.of(city));
        doNothing().when(touristSpotMapper).updateEntityFromDTO(touristSpotUpdateDTO, touristSpot);
        when(tagService.convertNamesToTags(touristSpotUpdateDTO.tags())).thenReturn(Collections.emptySet());
        when(touristSpotRepository.save(touristSpot)).thenReturn(touristSpot);

        assertDoesNotThrow(() -> touristSpotService.updateTouristSpot(spotId, touristSpotUpdateDTO, userId));

        verify(touristSpotRepository, times(1)).findById(spotId);
        verify(touristSpotRepository, times(1)).save(touristSpot);
    }

    @Test
    void shouldDeleteTouristSpotWhenOwner() {
        UUID spotId = touristSpot.getTouristSpotId();
        UUID userId = userOwner.getId();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.of(touristSpot));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userOwner));
        doNothing().when(touristSpotRepository).delete(touristSpot);

        assertDoesNotThrow(() -> touristSpotService.deleteTouristSpot(spotId, userId));

        verify(touristSpotRepository, times(1)).delete(touristSpot);
    }

    @Test
    void shouldDeleteTouristSpotWhenAdmin() {
        UUID spotId = touristSpot.getTouristSpotId();
        UUID adminId = adminUser.getId();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.of(touristSpot));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        doNothing().when(touristSpotRepository).delete(touristSpot);

        assertDoesNotThrow(() -> touristSpotService.deleteTouristSpot(spotId, adminId));

        verify(touristSpotRepository, times(1)).delete(touristSpot);
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateTouristSpotAndThrowExceptionWhenManagerNotFound() {
        UUID userId = UUID.randomUUID();

        when(spotManagerRepository.findByUser_Id(userId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> touristSpotService.createTouristSpot(touristSpotCreateDTO, userId)
        );

        verify(touristSpotRepository, never()).save(any());
    }

    @Test
    void shouldNotCreateTouristSpotAndThrowExceptionWhenCityNotFound() {
        UUID userId = userOwner.getId();

        when(spotManagerRepository.findByUser_Id(userId)).thenReturn(Optional.of(spotManager));
        when(cityRepository.findById(touristSpotCreateDTO.cityId())).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> touristSpotService.createTouristSpot(touristSpotCreateDTO, userId)
        );

        verify(touristSpotRepository, never()).save(any());
    }

    @Test
    void shouldNotCreateTouristSpotAndThrowExceptionWhenInvalidTime() {
        UUID userId = userOwner.getId();

        TouristSpot invalidSpot = new TouristSpot();
        invalidSpot.setOpensAt(LocalTime.of(18, 0));
        invalidSpot.setClosesAt(LocalTime.of(8, 0)); // Fechamento antes da abertura

        when(spotManagerRepository.findByUser_Id(userId)).thenReturn(Optional.of(spotManager));
        when(cityRepository.findById(touristSpotCreateDTO.cityId())).thenReturn(Optional.of(city));
        when(touristSpotMapper.toEntity(touristSpotCreateDTO)).thenReturn(invalidSpot);

        assertThrows(
                InvalidDateException.class,
                () -> touristSpotService.createTouristSpot(touristSpotCreateDTO, userId)
        );

        verify(touristSpotRepository, never()).save(any());
    }

    @Test
    void shouldNotGetTouristSpotAndThrowExceptionWhenNotFound() {
        UUID spotId = UUID.randomUUID();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> touristSpotService.getTouristSpot(spotId)
        );

        verify(touristSpotRepository, times(1)).findById(spotId);
    }

    @Test
    void shouldNotUpdateTouristSpotAndThrowExceptionWhenNotOwner() {
        UUID spotId = touristSpot.getTouristSpotId();
        UUID otherUserId = otherUser.getId();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.of(touristSpot));

        assertThrows(
                UserIsNotOwnerException.class,
                () -> touristSpotService.updateTouristSpot(spotId, touristSpotUpdateDTO, otherUserId)
        );

        verify(touristSpotRepository, never()).save(any());
    }

    @Test
    void shouldNotDeleteTouristSpotAndThrowExceptionWhenNotAdminNorOwner() {
        UUID spotId = touristSpot.getTouristSpotId();
        UUID otherUserId = otherUser.getId();

        when(touristSpotRepository.findById(spotId)).thenReturn(Optional.of(touristSpot));
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));

        assertThrows(
                UserIsNotAdminOrOwnerException.class,
                () -> touristSpotService.deleteTouristSpot(spotId, otherUserId)
        );

        verify(touristSpotRepository, never()).delete(any());
    }
}
