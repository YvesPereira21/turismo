package io.turismo.backend.service;

import io.turismo.backend.dto.state.StateCreateDTO;
import io.turismo.backend.dto.state.StateDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.mapper.StateMapper;
import io.turismo.backend.model.State;
import io.turismo.backend.repository.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StateServiceTest {

    @Mock
    private StateRepository stateRepository;
    @Mock
    private StateMapper stateMapper;

    @InjectMocks
    private StateService stateService;

    private State state;
    private StateDTO stateDTO;
    private StateCreateDTO stateCreateDTO;

    @BeforeEach
    void setUp() {
        state = new State();
        ReflectionTestUtils.setField(state, "stateId", UUID.randomUUID());
        state.setName("São Paulo");

        stateDTO = new StateDTO(state.getStateId(), state.getName());
        stateCreateDTO = new StateCreateDTO("São Paulo");
    }

    // -----------------------HAPPY PATH------------------------------

    @Test
    void shouldCreateState() {
        when(stateRepository.existsByNameIgnoreCase(stateCreateDTO.name())).thenReturn(false);
        when(stateRepository.save(any(State.class))).thenReturn(state);

        assertDoesNotThrow(() -> stateService.createState(stateCreateDTO));

        verify(stateRepository, times(1)).existsByNameIgnoreCase(stateCreateDTO.name());
        verify(stateRepository, times(1)).save(any(State.class));
    }

    @Test
    void shouldGetAllStates() {
        when(stateRepository.findAll()).thenReturn(List.of(state));
        when(stateMapper.toDTO(state)).thenReturn(stateDTO);

        Set<StateDTO> result = stateService.getAllStates();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(stateDTO));

        verify(stateRepository, times(1)).findAll();
        verify(stateMapper, times(1)).toDTO(state);
    }

    @Test
    void shouldDeleteState() {
        UUID stateId = state.getStateId();
        when(stateRepository.findById(stateId)).thenReturn(Optional.of(state));
        doNothing().when(stateRepository).delete(state);

        assertDoesNotThrow(() -> stateService.deleteState(stateId));

        verify(stateRepository, times(1)).findById(stateId);
        verify(stateRepository, times(1)).delete(state);
    }

    // -----------------------UNHAPPY PATH------------------------------

    @Test
    void shouldNotCreateStateAndThrowExceptionWhenStateAlreadyExists() {
        when(stateRepository.existsByNameIgnoreCase(stateCreateDTO.name())).thenReturn(true);

        assertThrows(
                ObjectAlreadyExistsException.class,
                () -> stateService.createState(stateCreateDTO)
        );

        verify(stateRepository, times(1)).existsByNameIgnoreCase(stateCreateDTO.name());
        verify(stateRepository, never()).save(any(State.class));
    }

    @Test
    void shouldNotDeleteStateAndThrowExceptionWhenStateNotFound() {
        UUID stateId = UUID.randomUUID();
        when(stateRepository.findById(stateId)).thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> stateService.deleteState(stateId)
        );

        verify(stateRepository, times(1)).findById(stateId);
        verify(stateRepository, never()).delete(any(State.class));
    }
}
