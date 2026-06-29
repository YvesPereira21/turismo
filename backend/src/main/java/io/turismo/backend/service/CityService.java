package io.turismo.backend.service;

import io.turismo.backend.dto.city.CityCreateDTO;
import io.turismo.backend.dto.city.CityDTO;
import io.turismo.backend.mapper.CityMapper;
import io.turismo.backend.model.City;
import io.turismo.backend.model.State;
import io.turismo.backend.model.enums.StateName;
import io.turismo.backend.repository.CityRepository;
import io.turismo.backend.repository.StateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    public CityDTO createCity(CityCreateDTO dto) {
        State state = stateRepository.findByName(dto.stateName())
                .orElseThrow(() -> new RuntimeException("Estado não encontrado"));
        boolean cityExists = cityRepository.existsByNameAndState_Name(dto.name(), dto.stateName());

        if(cityExists) {
            throw new RuntimeException("Essa cidade já existe");
        }

        City newCity = cityMapper.toEntity(dto);
        newCity.setState(state);

        return cityMapper.toDto(cityRepository.save(newCity));
    }

    public CityDTO getCity(String cityName, StateName stateName) {
        return cityMapper.toDto(
                cityRepository.findByNameAndState_Name(cityName, stateName)
                        .orElseThrow(() -> new RuntimeException("Essa cidade não existe"))
        );
    }

    public Page<CityDTO> getCitiesFromState(StateName stateName, Pageable pageable) {
        return cityRepository.findAllByState_Name(stateName, pageable).map(cityMapper::toDto);
    }

    public void deleteCity(UUID cityId){
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new RuntimeException("Essa cidade não existe"));

        cityRepository.delete(city);
    }
}
