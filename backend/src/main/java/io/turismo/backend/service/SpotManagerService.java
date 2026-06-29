package io.turismo.backend.service;

import io.turismo.backend.dto.spot_manager.SpotManagerCreateDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerSimpleDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerUpdateDTO;
import io.turismo.backend.mapper.SpotManagerMapper;
import io.turismo.backend.model.SpotManager;
import io.turismo.backend.repository.SpotManagerRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class SpotManagerService{
    private final SpotManagerRepository spotManagerRepository;
    private final SpotManagerMapper spotManagerMapper;
    private final UserService userService;

    public SpotManagerService(SpotManagerRepository spotManagerRepository, SpotManagerMapper spotManagerMapper, UserService userService) {
        this.spotManagerRepository = spotManagerRepository;
        this.spotManagerMapper = spotManagerMapper;
        this.userService = userService;
    }

    public SpotManagerSimpleDTO createSpotManager(SpotManagerCreateDTO dto){
        userService.verifyUserAlreadyExists(dto.user().email());

        SpotManager newSpotManager = spotManagerMapper.toEntity(dto);
        newSpotManager.getUser().setSpotManager(newSpotManager);
        newSpotManager.getSocialsMedia().forEach(socialMedia -> socialMedia.setSpotManager(newSpotManager));

        return spotManagerMapper.toSimpleDTO(spotManagerRepository.save(newSpotManager));
    }

    public SpotManagerSimpleDTO getSpotManager(UUID spotManagerId) {
        return spotManagerMapper.toSimpleDTO(
                spotManagerRepository.findById(spotManagerId)
                        .orElseThrow(() -> new RuntimeException("Gerente não encontrado"))
        );
    }

    public SpotManagerDTO currentSpotManager(UUID spotManagerId) {
        SpotManager spotManager = spotManagerRepository.findById(spotManagerId)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));

        return spotManagerMapper.toDTO(spotManager);
    }

    public SpotManagerSimpleDTO updateSpotManager(SpotManagerUpdateDTO spotManagerUpdateDTO, UUID spotManagerId){
        SpotManager spotManager = spotManagerRepository.findById(spotManagerId)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));

        spotManagerMapper.updateEntityFromDto(spotManagerUpdateDTO, spotManager);

        SpotManager spotManagerUpdated = spotManagerRepository.save(spotManager);

        return spotManagerMapper.toSimpleDTO(spotManagerUpdated);
    }

    public void deleteSpotManager(UUID spotManagerId){
        SpotManager spotManager = spotManagerRepository.findById(spotManagerId)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));

        spotManagerRepository.delete(spotManager);
    }
}