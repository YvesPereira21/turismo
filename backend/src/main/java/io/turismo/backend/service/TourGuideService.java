package io.turismo.backend.service;

import io.turismo.backend.dto.tour_guide.TourGuideCreateDTO;
import io.turismo.backend.dto.tour_guide.TourGuideDTO;
import io.turismo.backend.dto.tour_guide.TourGuideUpdateDTO;
import io.turismo.backend.mapper.TourGuideMapper;
import io.turismo.backend.model.TourGuide;
import io.turismo.backend.repository.TourGuideRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TourGuideService {
    private final TourGuideRepository tourGuideRepository;
    private final TourGuideMapper tourGuideMapper;
    private final UserService userService;

    public TourGuideService(TourGuideRepository tourGuideRepository, TourGuideMapper tourGuideMapper, UserService userService) {
        this.tourGuideRepository = tourGuideRepository;
        this.tourGuideMapper = tourGuideMapper;
        this.userService = userService;
    }

    public TourGuideDTO createTourGuide(TourGuideCreateDTO dto) {
        userService.verifyUserAlreadyExists(dto.user().email());
        boolean cadasturExists = tourGuideRepository.existsByCadastur(dto.cadastur());

        if(cadasturExists) {
            throw new RuntimeException("Cadastur já cadastrado");
        }

        TourGuide newTourGuide = tourGuideMapper.toEntity(dto);
        newTourGuide.getUser().setTourGuide(newTourGuide);

        return tourGuideMapper.toDTO(tourGuideRepository.save(newTourGuide));
    }

    public TourGuideDTO getTourGuide(UUID tourGuideId) {
        TourGuide tourGuide = tourGuideRepository.findById(tourGuideId)
                .orElseThrow(() -> new RuntimeException("Guia de Turismo não encontrado"));

        return tourGuideMapper.toDTO(tourGuide);
    }

    public TourGuideDTO updateTourGuide(TourGuideUpdateDTO tourGuideUpdate, UUID tourGuideId) {
        TourGuide tourGuide = tourGuideRepository.findById(tourGuideId)
                .orElseThrow(() -> new RuntimeException("Guia de Turismo não encontrado"));

        tourGuideMapper.updateEntityFromDto(tourGuideUpdate, tourGuide);

        TourGuide tourGuideUpdated = tourGuideRepository.save(tourGuide);

        return tourGuideMapper.toDTO(tourGuideUpdated);
    }

    public void deleteTourGuide(UUID tourGuideId) {
        TourGuide tourGuide = tourGuideRepository.findById(tourGuideId)
                .orElseThrow(() -> new RuntimeException("Guia de Turismo não encontrado"));

        tourGuideRepository.delete(tourGuide);
    }
}