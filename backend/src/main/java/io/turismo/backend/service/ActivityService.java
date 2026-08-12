package io.turismo.backend.service;

import io.turismo.backend.dto.activity.ActivityCreateDTO;
import io.turismo.backend.dto.activity.ActivityDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.mapper.ActivityMapper;
import io.turismo.backend.model.Activity;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.ActivityRepository;
import io.turismo.backend.repository.TouristSpotRepository;
import io.turismo.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final TouristSpotRepository touristSpotRepository;
    private final UserRepository userRepository;

    public ActivityService(ActivityRepository activityRepository, ActivityMapper activityMapper, TouristSpotRepository touristSpotRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
        this.touristSpotRepository = touristSpotRepository;
        this.userRepository = userRepository;
    }

    public ActivityDTO createActivity(UUID touristSpotId, UUID userId, ActivityCreateDTO dto) {
        TouristSpot touristSpot = touristSpotRepository.findById(touristSpotId)
                .orElseThrow(() -> new ObjectNotFoundException("Ponto turístico não encontrado"));

        if (!touristSpot.getSpotManager().getUser().getId().equals(userId)) {
            throw new UserIsNotOwnerException("Você não tem autorização para isso");
        }

        Activity activity = activityMapper.toEntity(dto);
        activity.setTouristSpot(touristSpot);
        
        return activityMapper.toDTO(activityRepository.save(activity));
    }

    public Set<ActivityDTO> getActivitiesByTouristSpotId(UUID touristSpotId) {
        if (!touristSpotRepository.existsById(touristSpotId)) {
            throw new ObjectNotFoundException("Ponto turístico não encontrado");
        }
        return activityRepository.findAllByTouristSpot_TouristSpotId(touristSpotId)
                .stream()
                .map(activityMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public void updateActivity(UUID activityId, UUID userId, ActivityCreateDTO dto) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ObjectNotFoundException("Atividade não encontrada"));
                
        TouristSpot touristSpot = activity.getTouristSpot();

        if (!touristSpot.getSpotManager().getUser().getId().equals(userId)) {
            throw new UserIsNotOwnerException("Você não tem autorização para isso");
        }

        activityMapper.updateEntityFromDTO(dto, activity);
        activityRepository.save(activity);
    }

    public void deleteActivity(UUID activityId, UUID userId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ObjectNotFoundException("Atividade não encontrada"));
                
        TouristSpot touristSpot = activity.getTouristSpot();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não existe"));

        if (!touristSpot.getSpotManager().getUser().getId().equals(userId) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new UserIsNotAdminOrOwnerException("Você não tem autorização para isso");
        }

        activityRepository.delete(activity);
    }
}