package io.turismo.backend.service;

import io.turismo.backend.dto.tourist.TouristCreateDTO;
import io.turismo.backend.dto.tourist.TouristDTO;
import io.turismo.backend.dto.tourist.TouristUpdateDTO;
import io.turismo.backend.mapper.TouristMapper;
import io.turismo.backend.model.Tourist;
import io.turismo.backend.repository.TouristRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TouristService{
    private final TouristRepository touristRepository;
    private final TouristMapper touristMapper;
    private final UserService userService;

    public TouristService(TouristRepository touristRepository, TouristMapper touristMapper, UserService userService) {
        this.touristRepository = touristRepository;
        this.touristMapper = touristMapper;
        this.userService = userService;
    }

    public TouristDTO createTourist(TouristCreateDTO dto) {
        userService.verifyUserAlreadyExists(dto.user().email());

        Tourist tourist = touristMapper.toEntity(dto);
        tourist.getUser().setTourist(tourist);

        return touristMapper.toDTO(touristRepository.save(tourist));
    }

    public TouristDTO getTourist(UUID touristId) {
        return touristMapper.toDTO(
                touristRepository.findById(touristId)
                .orElseThrow(() -> new RuntimeException("Não encontrado"))
        );
    }

    public TouristDTO updateTourist(TouristUpdateDTO touristUpdateDTO, UUID touristId) {
        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new RuntimeException("Não encontrado"));

        touristMapper.updateEntityFromDTO(touristUpdateDTO, tourist);

        return touristMapper.toDTO(touristRepository.save(tourist));
    }

    public void deleteTourist(UUID touristId) {
        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new RuntimeException("Não encontrado"));

        touristRepository.delete(tourist);
    }
}