package io.turismo.backend.service;

import io.turismo.backend.dto.tourist.TouristCreateDTO;
import io.turismo.backend.dto.tourist.TouristDTO;
import io.turismo.backend.dto.tourist.TouristUpdateDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.exception.InvalidDateException;
import io.turismo.backend.mapper.TouristMapper;
import io.turismo.backend.model.Tourist;
import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.TouristRepository;
import io.turismo.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TouristService{
    private final TouristRepository touristRepository;
    private final TouristMapper touristMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public TouristService(TouristRepository touristRepository, TouristMapper touristMapper, UserService userService, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.touristRepository = touristRepository;
        this.touristMapper = touristMapper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public TouristDTO createTourist(TouristCreateDTO dto) {
        userService.verifyUserAlreadyExists(dto.user().email());

        Tourist tourist = touristMapper.toEntity(dto);

        if (!ageIsValid(tourist.getBirthDate())) {
            throw new InvalidDateException("Data de nascimento inválida ou idade maior que 125 anos");
        }

        tourist.getUser().setTourist(tourist);
        tourist.getUser().setRole(UserRole.TOURIST);
        String encodedPassword = bCryptPasswordEncoder.encode(dto.user().password());
        tourist.getUser().setPassword(encodedPassword);

        return touristMapper.toDTO(touristRepository.save(tourist));
    }

    public TouristDTO getTourist(UUID touristId) {
        return touristMapper.toDTO(
                touristRepository.findById(touristId)
                .orElseThrow(() -> new ObjectNotFoundException("Não encontrado"))
        );
    }

    public TouristDTO updateTourist(TouristUpdateDTO touristUpdateDTO, UUID touristId, UUID userId) {
        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new ObjectNotFoundException("Não encontrado"));

        if(!tourist.getUser().getId().equals(userId)){
            throw new UserIsNotOwnerException("Você não tem autorização para isso");
        }

        touristMapper.updateEntityFromDTO(touristUpdateDTO, tourist);

        if (!ageIsValid(tourist.getBirthDate())) {
            throw new InvalidDateException("Data de nascimento inválida ou idade maior que 125 anos");
        }

        return touristMapper.toDTO(touristRepository.save(tourist));
    }

    public void deleteTourist(UUID touristId, UUID userId) {
        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new ObjectNotFoundException("Não encontrado"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));

        if(!tourist.getUser().getId().equals(userId) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new UserIsNotAdminOrOwnerException("Você não tem autorização para isso");
        }

        touristRepository.delete(tourist);
    }

    private boolean ageIsValid(LocalDate birthDate) {
        if (birthDate == null) return false;
        LocalDate now = LocalDate.now();
        return !birthDate.isBefore(now.minusYears(125)) && !birthDate.isAfter(now);
    }
}