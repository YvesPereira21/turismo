package io.turismo.backend.service;

import io.turismo.backend.dto.spot_manager.SpotManagerCreateDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerSimpleDTO;
import io.turismo.backend.dto.spot_manager.SpotManagerUpdateDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.mapper.SpotManagerMapper;
import io.turismo.backend.model.SpotManager;
import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.SpotManagerRepository;
import io.turismo.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class SpotManagerService{
    private final SpotManagerRepository spotManagerRepository;
    private final SpotManagerMapper spotManagerMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SpotManagerService(SpotManagerRepository spotManagerRepository, SpotManagerMapper spotManagerMapper, UserService userService, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.spotManagerRepository = spotManagerRepository;
        this.spotManagerMapper = spotManagerMapper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public SpotManagerSimpleDTO createSpotManager(SpotManagerCreateDTO dto){
        userService.verifyUserAlreadyExists(dto.user().email());

        SpotManager newSpotManager = spotManagerMapper.toEntity(dto);
        newSpotManager.getUser().setSpotManager(newSpotManager);
        newSpotManager.getUser().setRole(UserRole.SPOTMANAGER);
        String encodedPassword = bCryptPasswordEncoder.encode(dto.user().password());
        newSpotManager.getUser().setPassword(encodedPassword);


        return spotManagerMapper.toSimpleDTO(spotManagerRepository.save(newSpotManager));
    }

    public SpotManagerSimpleDTO getSpotManager(UUID spotManagerId) {
        return spotManagerMapper.toSimpleDTO(
                spotManagerRepository.findById(spotManagerId)
                        .orElseThrow(() -> new ObjectNotFoundException("Gerente não encontrado"))
        );
    }

    public SpotManagerDTO currentSpotManager(UUID spotManagerId) {
        SpotManager spotManager = spotManagerRepository.findById(spotManagerId)
                .orElseThrow(() -> new ObjectNotFoundException("Gerente não encontrado"));

        return spotManagerMapper.toDTO(spotManager);
    }

    public SpotManagerSimpleDTO updateSpotManager(SpotManagerUpdateDTO spotManagerUpdateDTO, UUID spotManagerId, UUID userId){
        SpotManager spotManager = spotManagerRepository.findById(spotManagerId)
                .orElseThrow(() -> new ObjectNotFoundException("Gerente não encontrado"));

        if(!spotManager.getUser().getId().equals(userId)) {
            throw new UserIsNotOwnerException("Você não tem autorização para isso");
        }

        spotManagerMapper.updateEntityFromDto(spotManagerUpdateDTO, spotManager);

        SpotManager spotManagerUpdated = spotManagerRepository.save(spotManager);

        return spotManagerMapper.toSimpleDTO(spotManagerUpdated);
    }

    public void deleteSpotManager(UUID spotManagerId, UUID userId){
        SpotManager spotManager = spotManagerRepository.findById(spotManagerId)
                .orElseThrow(() -> new ObjectNotFoundException("Gerente não encontrado"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));

        if(!spotManager.getUser().getId().equals(userId) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new UserIsNotAdminOrOwnerException("Você não tem autorização para isso");
        }

        spotManagerRepository.delete(spotManager);
    }
}