package io.turismo.backend.service;

import io.turismo.backend.dto.tour_guide.TourGuideCreateDTO;
import io.turismo.backend.dto.tour_guide.TourGuideDTO;
import io.turismo.backend.dto.tour_guide.TourGuideUpdateDTO;
import io.turismo.backend.exception.ObjectAlreadyExistsException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.mapper.TourGuideMapper;
import io.turismo.backend.model.TourGuide;
import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.TourGuideRepository;
import io.turismo.backend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TourGuideService {
    private final TourGuideRepository tourGuideRepository;
    private final TourGuideMapper tourGuideMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public TourGuideService(TourGuideRepository tourGuideRepository, TourGuideMapper tourGuideMapper, UserService userService, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.tourGuideRepository = tourGuideRepository;
        this.tourGuideMapper = tourGuideMapper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public TourGuideDTO createTourGuide(TourGuideCreateDTO dto) {
        userService.verifyUserAlreadyExists(dto.user().email());
        boolean cadasturExists = tourGuideRepository.existsByCadastur(dto.cadastur());

        if(cadasturExists) {
            throw new ObjectAlreadyExistsException("Cadastur já cadastrado");
        }

        TourGuide tourGuide = tourGuideMapper.toEntity(dto);
        tourGuide.getUser().setTourGuide(tourGuide);
        tourGuide.getUser().setRole(UserRole.TOURGUIDE);
        String encodedPassword = bCryptPasswordEncoder.encode(dto.user().password());
        tourGuide.getUser().setPassword(encodedPassword);

        return tourGuideMapper.toDTO(tourGuideRepository.save(tourGuide));
    }

    @Cacheable(value = "guia_turismo", key = "#tourGuideId", sync = true)
    public TourGuideDTO getTourGuide(UUID tourGuideId) {
        TourGuide tourGuide = tourGuideRepository.findById(tourGuideId)
                .orElseThrow(() -> new ObjectNotFoundException("Guia de Turismo não encontrado"));

        return tourGuideMapper.toDTO(tourGuide);
    }

    @Cacheable(value = "guias_turismo_ponto_turistico", sync = true)
    public Page<TourGuideDTO> getTourGuidesByTouristSpot(UUID touristSpotId, Pageable pageable) {
        return tourGuideRepository.findAllByTouristSpots_TouristSpotId(touristSpotId, pageable)
                .map(tourGuideMapper::toDTO);
    }

    @Caching(
            put = { @CachePut(value = "guia_turismo", key = "#tourGuideId") },
            evict = { @CacheEvict(value = "guias_turismo_ponto_turistico", allEntries = true) }
    )
    public TourGuideDTO updateTourGuide(TourGuideUpdateDTO tourGuideUpdate, UUID tourGuideId, UUID userId) {
        TourGuide tourGuide = tourGuideRepository.findById(tourGuideId)
                .orElseThrow(() -> new ObjectNotFoundException("Guia de Turismo não encontrado"));

        if(!tourGuide.getUser().getId().equals(userId)) {
            throw new UserIsNotOwnerException("Você não tem autorização para isso");
        }

        if (tourGuideUpdate.cadastur() != null && !tourGuideUpdate.cadastur().equals(tourGuide.getCadastur())) {
            boolean cadasturExists = tourGuideRepository.existsByCadastur(tourGuideUpdate.cadastur());
            if(cadasturExists) {
                throw new ObjectAlreadyExistsException("Cadastur já cadastrado");
            }
        }

        tourGuideMapper.updateEntityFromDto(tourGuideUpdate, tourGuide);
        TourGuide tourGuideUpdated = tourGuideRepository.save(tourGuide);

        return tourGuideMapper.toDTO(tourGuideUpdated);
    }

    @Caching(evict = {
            @CacheEvict(value = "guia_turismo", key = "#tourGuideId"),
            @CacheEvict(value = "guias_turismo_ponto_turistico", allEntries = true)
    })
    public void deleteTourGuide(UUID tourGuideId, UUID userId) {
        TourGuide tourGuide = tourGuideRepository.findById(tourGuideId)
                .orElseThrow(() -> new ObjectNotFoundException("Guia de Turismo não encontrado"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));

        if(!tourGuide.getUser().getId().equals(userId) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new UserIsNotAdminOrOwnerException("Você não tem autorização para isso");
        }

        tourGuideRepository.delete(tourGuide);
    }
}