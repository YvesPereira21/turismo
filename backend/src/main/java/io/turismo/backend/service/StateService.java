package io.turismo.backend.service;

import io.turismo.backend.dto.state.StateCreateDTO;
import io.turismo.backend.dto.state.StateDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.mapper.StateMapper;
import io.turismo.backend.model.State;
import io.turismo.backend.repository.StateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StateService{
    private final StateRepository stateRepository;
    private final StateMapper stateMapper;

    public StateService(StateRepository stateRepository, StateMapper stateMapper) {
        this.stateRepository = stateRepository;
        this.stateMapper = stateMapper;
    }

    @CacheEvict(value = "estados", allEntries = true)
    public void createState(StateCreateDTO dto) {
        log.info("Creating state: {}", dto.name());
        boolean stateExists = stateRepository.existsByNameIgnoreCase(dto.name());

        if(stateExists){
            throw new ObjectAlreadyExistsException("Estado com esse nome " + dto.name() + " já existe");
        }

        State newState = new State();
        newState.setName(dto.name());

        stateRepository.save(newState);
    }

    @Cacheable(value = "estados", sync = true)
    public Set<StateDTO> getAllStates() {
        log.info("Fetching all states");
        return stateRepository.findAll()
                .stream()
                .map(stateMapper::toDTO)
                .collect(Collectors.toSet());
    }

    @CacheEvict(value = "estados", allEntries = true)
    public void deleteState(UUID stateId) {
        log.info("Deleting state ID: {}", stateId);
        State state = stateRepository.findById(stateId)
                .orElseThrow(() -> new ObjectNotFoundException("Estado não encontrado"));

        stateRepository.delete(state);
    }
}
