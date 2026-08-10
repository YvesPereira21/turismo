package io.turismo.backend.service;

import io.turismo.backend.dto.geojson.GeoFeatureCollectionDTO;
import io.turismo.backend.dto.geojson.GeoFeatureDTO;
import io.turismo.backend.dto.tourist_spot.*;
import io.turismo.backend.exception.UserIsNotAdminOrOwnerException;
import io.turismo.backend.exception.UserIsNotOwnerException;
import io.turismo.backend.exception.ObjectNotFoundException;
import io.turismo.backend.exception.InvalidDateException;
import io.turismo.backend.mapper.TouristSpotMapper;
import io.turismo.backend.model.City;
import io.turismo.backend.model.SpotManager;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.repository.CityRepository;
import io.turismo.backend.repository.SpotManagerRepository;
import io.turismo.backend.repository.TouristSpotRepository;
import io.turismo.backend.repository.UserRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class TouristSpotService{
    private final TouristSpotRepository touristSpotRepository;
    private final TouristSpotMapper touristSpotMapper;
    private final SpotManagerRepository spotManagerRepository;
    private final CityRepository cityRepository;
    private final TagService tagService;
    private final UserRepository userRepository;

    public TouristSpotService(TouristSpotRepository touristSpotRepository, TouristSpotMapper touristSpotMapper, SpotManagerRepository spotManagerRepository, CityRepository cityRepository, TagService tagService, UserRepository userRepository) {
        this.touristSpotRepository = touristSpotRepository;
        this.touristSpotMapper = touristSpotMapper;
        this.spotManagerRepository = spotManagerRepository;
        this.cityRepository = cityRepository;
        this.tagService = tagService;
        this.userRepository = userRepository;
    }

    @Caching(evict = {
            @CacheEvict(value = "pontos", allEntries = true),
            @CacheEvict(value = "pontos_mapa", allEntries = true),
            @CacheEvict(value = "pontos_gestor", allEntries = true)
    })
    public TouristSpotDTO createTouristSpot(TouristSpotCreateDTO dto, UUID userId){
        SpotManager spotManager = spotManagerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Gerente não encontrado"));
        City city = cityRepository.findById(dto.cityId())
                .orElseThrow(() -> new ObjectNotFoundException("Cidade não encontrada"));

        if (isTimeInvalid(dto.opensAt(), dto.closesAt())) {
            throw new InvalidDateException("Horário de fechamento não pode ser antes do horário de abertura");
        }

        TouristSpot touristSpot = touristSpotMapper.toEntity(dto);
        touristSpot.setTags(tagService.convertNamesToTags(dto.tags()));
        touristSpot.setCity(city);
        touristSpot.setSpotManager(spotManager);

        if (touristSpot.getSocialsMedia() != null) {
            touristSpot.getSocialsMedia().forEach(socialMedia -> socialMedia.setTouristSpot(touristSpot));
        }

        return touristSpotMapper.toDTO(touristSpotRepository.save(touristSpot));
    }

    @Cacheable(value = "ponto", key = "#touristSpotId", sync = true)
    public TouristSpotDTO getTouristSpot(UUID touristSpotId){
        return touristSpotMapper.toDTO(
                touristSpotRepository
                        .findById(touristSpotId)
                        .orElseThrow(() -> new ObjectNotFoundException("Ponto turístico não encontrado"))
        );
    }

    @Cacheable(value = "pontos_mapa", sync = true)
    public GeoFeatureCollectionDTO<TouristSpotToMapDTO> getTouristSpotsToMap(){
        List<GeoFeatureDTO<TouristSpotToMapDTO>> features = touristSpotRepository.findAll()
                .stream()
                .map(spot -> {
                    var properties = new TouristSpotToMapDTO(spot.getTouristSpotId(), spot.getName());

                    return new GeoFeatureDTO<>(properties, spot.getLocation());
                }).toList();

        return new GeoFeatureCollectionDTO<>(features);
    }

    @Cacheable(value = "pontos", sync = true)
    public Page<TouristSpotListDTO> getTouristSpots(Pageable pageable){
        return touristSpotRepository.findAll(pageable).map(touristSpotMapper::toListDTO);
    }

    @Cacheable(value = "pontos", sync = true)
    public Page<TouristSpotListDTO> getTouristSpotsFromState(String stateName, Pageable pageable){
        return touristSpotRepository.findAllByStateName(stateName, pageable).map(touristSpotMapper::toListDTO);
    }

    @Cacheable(value = "pontos_gestor", sync = true)
    public Page<TouristSpotListDTO> getSpotManagerTouristSpots(UUID spotManagerId, Pageable pageable){
        return touristSpotRepository
                .findAllBySpotManager_SpotManagerId(spotManagerId, pageable)
                .map(touristSpotMapper::toListDTO);
    }

    @Cacheable(value = "pontos", sync = true)
    public Page<TouristSpotListDTO> getNearTouristSpots(
            Double longitude,
            Double latitude,
            Double radius,
            Pageable pageable
    ){
        GeometryFactory geometry = new GeometryFactory(new PrecisionModel(), 4326);
        Point userLocation = geometry.createPoint(new Coordinate(longitude, latitude));

        return touristSpotRepository
                .findAllPointsNearUserWithinCertainRadius(userLocation, radius, pageable)
                .map(proj ->
                        touristSpotMapper.toListWithDistanceDTO(proj.getTouristSpot(), proj.getDistance())
                );
    }

    @Caching(evict = {
            @CacheEvict(value = "pontos", allEntries = true),
            @CacheEvict(value = "pontos_mapa", allEntries = true),
            @CacheEvict(value = "pontos_gestor", allEntries = true),
            @CacheEvict(value = "ponto", key = "#touristSpotId")
    })
    public void updateTouristSpot(UUID touristSpotId, TouristSpotUpdateDTO touristSpotUpdate, UUID userId){
        TouristSpot touristSpot = touristSpotRepository.findById(touristSpotId)
                .orElseThrow(() -> new ObjectNotFoundException("Ponto turístico não encontrado"));

        if (!touristSpot.getSpotManager().getUser().getId().equals(userId)) {
            throw new UserIsNotOwnerException("Você não tem autorização para isso");
        }

        if(touristSpotUpdate.cityId() != null){
            City city = cityRepository.findById(touristSpotUpdate.cityId())
                    .orElseThrow(() -> new ObjectNotFoundException("Cidade não encontrada"));

            touristSpot.setCity(city);
        }

        touristSpotMapper.updateEntityFromDTO(touristSpotUpdate, touristSpot);
        
        if (isTimeInvalid(touristSpot.getOpensAt(), touristSpot.getClosesAt())) {
            throw new InvalidDateException("Horário de fechamento não pode ser antes do horário de abertura");
        }

        touristSpot.setTags(tagService.convertNamesToTags(touristSpotUpdate.tags()));

        if (touristSpot.getSocialsMedia() != null) {
            touristSpot.getSocialsMedia().forEach(socialMedia -> socialMedia.setTouristSpot(touristSpot));
        }

        touristSpotRepository.save(touristSpot);
    }

    @Caching(evict = {
            @CacheEvict(value = "pontos", allEntries = true),
            @CacheEvict(value = "pontos_mapa", allEntries = true),
            @CacheEvict(value = "pontos_gestor", allEntries = true),
            @CacheEvict(value = "ponto", key = "#touristSpotId")
    })
    public void deleteTouristSpot(UUID touristSpotId, UUID userId){
        TouristSpot touristSpot = touristSpotRepository.findById(touristSpotId)
                .orElseThrow(() -> new ObjectNotFoundException("Ponto turístico não encontrado"));
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ObjectNotFoundException("Usuário não existe"));
        SpotManager spotManager = touristSpot.getSpotManager();

        if (!spotManager.getUser().getId().equals(userId) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new UserIsNotAdminOrOwnerException("Você não tem autorização para isso");
        }

        touristSpotRepository.delete(touristSpot);
    }

    private boolean isTimeInvalid(LocalTime opensAt, LocalTime closesAt) {
        if (opensAt != null && closesAt != null) {
            return closesAt.isBefore(opensAt);
        }
        return false;
    }
}