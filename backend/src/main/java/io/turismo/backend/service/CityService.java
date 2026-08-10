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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.UUID;

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
        State state = stateRepository.findByName(dto.stateName())
                .orElseThrow(() -> new ObjectNotFoundException("Estado não encontrado"));
        boolean cityExists = cityRepository.existsByNameAndState_Name(dto.name(), dto.stateName());

        if(cityExists) {
            throw new ObjectAlreadyExistsException("Essa cidade já existe");
        }

        City newCity = cityMapper.toEntity(dto);
        newCity.setState(state);

        return cityMapper.toDto(cityRepository.save(newCity));
    }

    @Cacheable(value = "cidades", sync = true)
    public CityDTO getCity(String cityName, String stateName) {
        return cityMapper.toDto(
                cityRepository.findByNameAndState_Name(cityName, stateName)
                        .orElseThrow(() -> new ObjectNotFoundException("Essa cidade não existe"))
        );
    }

    @Cacheable(value = "cidades", sync = true)
    public Page<CityDTO> getCitiesFromState(String stateName, Pageable pageable) {
        return cityRepository.findAllByState_Name(stateName, pageable).map(cityMapper::toDto);
    }

    @CacheEvict(value = "cidades", allEntries = true)
    public void deleteCity(UUID cityId){
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ObjectNotFoundException("Essa cidade não existe"));

        cityRepository.delete(city);
    }
}
