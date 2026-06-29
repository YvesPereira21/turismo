package io.turismo.backend.service;

import io.turismo.backend.dto.state.StateCreateDTO;
import io.turismo.backend.dto.state.StateDTO;
import io.turismo.backend.mapper.StateMapper;
import io.turismo.backend.model.State;
import io.turismo.backend.repository.StateRepository;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StateService{
    private final StateRepository stateRepository;
    private final StateMapper stateMapper;

    public StateService(StateRepository stateRepository, StateMapper stateMapper) {
        this.stateRepository = stateRepository;
        this.stateMapper = stateMapper;
    }

    public void createState(StateCreateDTO dto) {
        boolean stateExists = stateRepository.existsByNameIgnoreCase(dto.name());

        if(stateExists){
            throw new RuntimeException("Estado com esse nome " + dto.name() + " já existe");
        }

        State newState = new State();
        newState.setName(dto.name());

        stateRepository.save(newState);
    }

    public Set<StateDTO> getAllStates() {
        return stateRepository.findAll()
                .stream()
                .map(stateMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public void deleteState(UUID stateId) {
        State state = stateRepository.findById(stateId)
                .orElseThrow(() -> new RuntimeException("Estado não encontrado"));

        stateRepository.delete(state);
    }
}
