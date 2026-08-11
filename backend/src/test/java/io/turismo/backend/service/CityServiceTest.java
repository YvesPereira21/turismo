package io.turismo.backend.service;

import io.turismo.backend.dto.city.CityCreateDTO;
import io.turismo.backend.dto.city.CityDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.mapper.CityMapper;
import io.turismo.backend.model.City;
import io.turismo.backend.model.State;
import io.turismo.backend.repository.CityRepository;
import io.turismo.backend.repository.StateRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;
    @Mock
    private CityMapper cityMapper;
    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private CityService cityService;

    private State state;
    private City city;
    private CityDTO cityDTO;
    private CityCreateDTO cityCreateDTO;

    @BeforeEach
    void setUp() {
        state = new State();
        ReflectionTestUtils.setField(state, "stateId", UUID.randomUUID());
        state.setName("São Paulo");

        city = new City();
        ReflectionTestUtils.setField(city, "cityId", UUID.randomUUID());
        city.setName("Campinas");
        city.setState(state);

        cityDTO = new CityDTO(city.getCityId(), city.getName(), state.getName());
        cityCreateDTO = new CityCreateDTO("Campinas", "São Paulo");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateCity() {
        when(stateRepository.findByName(cityCreateDTO.stateName())).thenReturn(Optional.of(state));
        when(cityRepository.existsByNameAndState_Name(cityCreateDTO.name(), cityCreateDTO.stateName())).thenReturn(false);
        when(cityMapper.toEntity(cityCreateDTO)).thenReturn(city);
        when(cityRepository.save(any(City.class))).thenReturn(city);
        when(cityMapper.toDto(city)).thenReturn(cityDTO);

        CityDTO result = cityService.createCity(cityCreateDTO);

        assertNotNull(result);
        assertEquals(cityDTO.name(), result.name());

        verify(stateRepository, times(1)).findByName(cityCreateDTO.stateName());
        verify(cityRepository, times(1)).existsByNameAndState_Name(cityCreateDTO.name(), cityCreateDTO.stateName());
        verify(cityRepository, times(1)).save(any(City.class));
    }

    @Test
    void shouldGetCity() {
        when(cityRepository.findByNameAndState_Name("Campinas", "São Paulo")).thenReturn(Optional.of(city));
        when(cityMapper.toDto(city)).thenReturn(cityDTO);

        CityDTO result = cityService.getCity("Campinas", "São Paulo");

        assertNotNull(result);
        assertEquals("Campinas", result.name());

        verify(cityRepository, times(1)).findByNameAndState_Name("Campinas", "São Paulo");
    }

    @Test
    void shouldGetCitiesFromState() {
        when(cityRepository.findAllByState_Name("São Paulo")).thenReturn(List.of(city));
        when(cityMapper.toDto(city)).thenReturn(cityDTO);

        List<CityDTO> result = cityService.getCitiesFromState("São Paulo");

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(cityRepository, times(1)).findAllByState_Name("São Paulo");
    }

    @Test
    void shouldDeleteCity() {
        UUID cityId = city.getCityId();
        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        doNothing().when(cityRepository).delete(city);

        assertDoesNotThrow(() -> cityService.deleteCity(cityId));

        verify(cityRepository, times(1)).findById(cityId);
        verify(cityRepository, times(1)).delete(city);
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateCityAndThrowExceptionWhenStateNotFound() {
        when(stateRepository.findByName(cityCreateDTO.stateName())).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> cityService.createCity(cityCreateDTO)
        );

        verify(stateRepository, times(1)).findByName(cityCreateDTO.stateName());
        verify(cityRepository, never()).save(any(City.class));
    }

    @Test
    void shouldNotCreateCityAndThrowExceptionWhenCityAlreadyExists() {
        when(stateRepository.findByName(cityCreateDTO.stateName())).thenReturn(Optional.of(state));
        when(cityRepository.existsByNameAndState_Name(cityCreateDTO.name(), cityCreateDTO.stateName())).thenReturn(true);

        assertThrows(
                ObjectAlreadyExistsException.class,
                () -> cityService.createCity(cityCreateDTO)
        );

        verify(cityRepository, times(1)).existsByNameAndState_Name(cityCreateDTO.name(), cityCreateDTO.stateName());
        verify(cityRepository, never()).save(any(City.class));
    }

    @Test
    void shouldNotGetCityAndThrowExceptionWhenCityNotFound() {
        when(cityRepository.findByNameAndState_Name("Inexistente", "São Paulo")).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> cityService.getCity("Inexistente", "São Paulo")
        );

        verify(cityRepository, times(1)).findByNameAndState_Name("Inexistente", "São Paulo");
    }

    @Test
    void shouldNotDeleteCityAndThrowExceptionWhenCityNotFound() {
        UUID cityId = UUID.randomUUID();
        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> cityService.deleteCity(cityId)
        );

        verify(cityRepository, times(1)).findById(cityId);
        verify(cityRepository, never()).delete(any(City.class));
    }
}
