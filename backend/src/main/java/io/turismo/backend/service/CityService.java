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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CityService{
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final StateRepository stateRepository;

    public CityService(CityRepository cityRepository, CityMapper cityMapper, StateRepository stateRepository) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.stateRepository = stateRepository;
    }

    @CacheEvict(value = "cidades", allEntries = true)
    public CityDTO createCity(CityCreateDTO dto) {
        log.info("Creating city: {} in state: {}", dto.name(), dto.stateName());
        State state = stateRepository.findByName(dto.stateName())
                .orElseThrow(() -> new ObjectNotFoundException("Estado não encontrado"));
        boolean cityExists = cityRepository.existsByNameAndState_Name(dto.name(), dto.stateName());

        if(cityExists) {
            throw new ObjectAlreadyExistsException("Essa cidade já existe");
        }

        City newCity = cityMapper.toEntity(dto);
        newCity.setState(state);

        City saved = cityRepository.save(newCity);
        log.info("City created with ID: {}", saved.getCityId());
        return cityMapper.toDto(saved);
    }

    public CityDTO getCity(String cityName, String stateName) {
        log.info("Fetching city: {} in state: {}", cityName, stateName);
        return cityMapper.toDto(
                cityRepository.findByNameAndState_Name(cityName, stateName)
                        .orElseThrow(() -> new ObjectNotFoundException("Essa cidade não existe"))
        );
    }

    public List<CityDTO> getCitiesFromState(String stateName) {
        log.info("Fetching cities for state: {}", stateName);
        return cityRepository.findAllByState_Name(stateName).stream()
                .map(cityMapper::toDto)
                .toList();
    }

    @CacheEvict(value = "cidades", allEntries = true)
    public void deleteCity(UUID cityId){
        log.info("Deleting city ID: {}", cityId);
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ObjectNotFoundException("Essa cidade não existe"));

        cityRepository.delete(city);
    }
}
