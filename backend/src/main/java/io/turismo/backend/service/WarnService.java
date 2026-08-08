package io.turismo.backend.service;

import io.turismo.backend.dto.warn.WarnCreateDTO;
import io.turismo.backend.dto.warn.WarnDTO;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.mapper.WarnMapper;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.model.Warn;
import io.turismo.backend.repository.TouristSpotRepository;
import io.turismo.backend.repository.WarnRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class WarnService {
    private final WarnRepository warnRepository;
    private final WarnMapper warnMapper;
    private final TouristSpotRepository touristSpotRepository;

    public WarnService(WarnRepository warnRepository, WarnMapper warnMapper, TouristSpotRepository touristSpotRepository) {
        this.warnRepository = warnRepository;
        this.warnMapper = warnMapper;
        this.touristSpotRepository = touristSpotRepository;
    }

    public WarnDTO createWarn(UUID userSpotManagerId, WarnCreateDTO dto, UUID touristSpotId){
        TouristSpot touristSpot = touristSpotRepository.findById(touristSpotId)
                .orElseThrow(() -> new ObjectNotFoundException("Ponto turístico não encontrado"));

        if(!touristSpot.getSpotManager().getUser().getId().equals(userSpotManagerId)){
            throw new UserIsNotOwnerException("Você não tem autorização para isso");
        }

        Warn warn = warnMapper.toEntity(dto);
        warn.setTouristSpot(touristSpot);
        warn.setEventDate(LocalDate.now());

        return warnMapper.toDTO(warnRepository.save(warn));
    }

    public WarnDTO getWarn(UUID warnId){
        return warnMapper.toDTO(
                warnRepository.findById(warnId)
                .orElseThrow(() -> new ObjectNotFoundException("Aviso não encontrado"))
        );
    }

    public Page<WarnDTO> getAllTouristSpotWarn(UUID touristSpotId, Pageable pageable){
        return warnRepository.findAllByTouristSpot_TouristSpotId(touristSpotId, pageable)
                .map(warnMapper::toDTO);
    }

    public void deleteWarn(UUID userSpotManagerId, UUID warnId) {
        Warn warn = warnRepository.findById(warnId)
                        .orElseThrow(() -> new ObjectNotFoundException("Aviso não encontrado"));
        TouristSpot touristSpot = warn.getTouristSpot();

        if(!touristSpot.getSpotManager().getUser().getId().equals(userSpotManagerId)){
            throw new UserIsNotOwnerException("Você não tem autorização para isso");
        }

        warnRepository.delete(warn);
    }
}