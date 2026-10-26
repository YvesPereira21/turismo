package io.turismo.backend.controller;

import io.turismo.backend.controller.docs.StateDocs;
import io.turismo.backend.dto.state.StateCreateDTO;
import io.turismo.backend.dto.state.StateDTO;
import io.turismo.backend.service.StateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class StateController implements StateDocs {

    private final StateService stateService;

    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/states")
    public ResponseEntity<Void> createState(@RequestBody StateCreateDTO dto) {
        stateService.createState(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @GetMapping("/states")
    public ResponseEntity<Set<StateDTO>> getAllStates() {
        Set<StateDTO> states = stateService.getAllStates();
        return ResponseEntity.ok(states);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/states/{stateId}")
    public ResponseEntity<Void> deleteState(@PathVariable UUID stateId) {
        stateService.deleteState(stateId);
        return ResponseEntity.noContent().build();
    }
}
