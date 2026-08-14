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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
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

    @Caching(evict = {
            @CacheEvict(value = "aviso_detalhe", allEntries = true),
            @CacheEvict(value = "avisos", allEntries = true)
    })
    public WarnDTO createWarn(UUID userSpotManagerId, WarnCreateDTO dto, UUID touristSpotId){
        log.info("Creating warn for tourist spot ID: {} by user ID: {}", touristSpotId, userSpotManagerId);
        TouristSpot touristSpot = touristSpotRepository.findById(touristSpotId)
                .orElseThrow(() -> new ObjectNotFoundException("Ponto turístico não encontrado"));

        if(!touristSpot.getSpotManager().getUser().getId().equals(userSpotManagerId)){
            throw new UserIsNotOwnerException("Você não tem autorização para isso");
        }

        Warn warn = warnMapper.toEntity(dto);
        warn.setTouristSpot(touristSpot);
        warn.setEventDate(LocalDate.now());

        Warn saved = warnRepository.save(warn);
        log.info("Warn created with ID: {}", saved.getId());
        return warnMapper.toDTO(saved);
    }

    @Cacheable(value = "aviso_detalhe", sync = true)
    public WarnDTO getWarn(UUID warnId){
        log.info("Fetching warn ID: {}", warnId);
        return warnMapper.toDTO(
                warnRepository.findById(warnId)
                .orElseThrow(() -> new ObjectNotFoundException("Aviso não encontrado"))
        );
    }

    public Page<WarnDTO> getAllTouristSpotWarn(UUID touristSpotId, Pageable pageable){
        log.info("Fetching warns for tourist spot ID: {}", touristSpotId);
        return warnRepository.findAllByTouristSpot_TouristSpotId(touristSpotId, pageable)
                .map(warnMapper::toDTO);
    }

    @Caching(evict = {
            @CacheEvict(value = "aviso_detalhe", allEntries = true),
            @CacheEvict(value = "avisos", allEntries = true)
    })
    public void deleteWarn(UUID userSpotManagerId, UUID warnId) {
        log.info("Deleting warn ID: {} by user ID: {}", warnId, userSpotManagerId);
        Warn warn = warnRepository.findById(warnId)
                        .orElseThrow(() -> new ObjectNotFoundException("Aviso não encontrado"));
        TouristSpot touristSpot = warn.getTouristSpot();

        if(!touristSpot.getSpotManager().getUser().getId().equals(userSpotManagerId)){
            throw new UserIsNotOwnerException("Você não tem autorização para isso");
        }

        warnRepository.delete(warn);
    }
}