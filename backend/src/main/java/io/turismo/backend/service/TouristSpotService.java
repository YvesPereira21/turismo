package io.turismo.backend.service;

import io.turismo.backend.dto.geojson.GeoFeatureCollectionDTO;
import io.turismo.backend.dto.geojson.GeoFeatureDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotCreateDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotListDTO;
import io.turismo.backend.dto.tourist_spot.TouristSpotToMapDTO;
import io.turismo.backend.mapper.TouristSpotMapper;
import io.turismo.backend.model.City;
import io.turismo.backend.model.SpotManager;
import io.turismo.backend.model.TouristSpot;
import io.turismo.backend.repository.CityRepository;
import io.turismo.backend.repository.SpotManagerRepository;
import io.turismo.backend.repository.TouristSpotRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class TouristSpotService{
    private final TouristSpotRepository touristSpotRepository;
    private final TouristSpotMapper touristSpotMapper;
    private final SpotManagerRepository spotManagerRepository;
    private final CityRepository cityRepository;
    private final TagService tagService;

    public TouristSpotService(TouristSpotRepository touristSpotRepository, TouristSpotMapper touristSpotMapper, SpotManagerRepository spotManagerRepository, CityRepository cityRepository, TagService tagService) {
        this.touristSpotRepository = touristSpotRepository;
        this.touristSpotMapper = touristSpotMapper;
        this.spotManagerRepository = spotManagerRepository;
        this.cityRepository = cityRepository;
        this.tagService = tagService;
    }

    public TouristSpotDTO createTouristSpot(TouristSpotCreateDTO dto, UUID spotManagerId){
        SpotManager spotManager = spotManagerRepository.findById(spotManagerId)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));
        City city = cityRepository.findById(dto.cityId())
                .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));

        TouristSpot touristSpot = touristSpotMapper.toEntity(dto);
        touristSpot.setTags(tagService.convertNamesToTags(dto.tags()));
        touristSpot.setCity(city);
        touristSpot.setSpotManager(spotManager);

        return touristSpotMapper.toDTO(touristSpotRepository.save(touristSpot));
    }

    public TouristSpotDTO getTouristSpot(UUID touristSpotId){
        return touristSpotMapper.toDTO(
                touristSpotRepository
                        .findById(touristSpotId)
                        .orElseThrow(() -> new RuntimeException("Ponto turístico não encontrado"))
        );
    }

    public GeoFeatureCollectionDTO<TouristSpotToMapDTO> getTouristSpotsToMap(){
        List<GeoFeatureDTO<TouristSpotToMapDTO>> features = touristSpotRepository.findAll()
                .stream()
                .map(spot -> {
                    var properties = new TouristSpotToMapDTO(spot.getTouristSpotId(), spot.getName());

                    return new GeoFeatureDTO<>(properties, spot.getLocation());
                })
                .toList();

        return new GeoFeatureCollectionDTO<>(features);
    }

    public Page<TouristSpotListDTO> getTouristSpots(Pageable pageable){
        return touristSpotRepository.findAll(pageable).map(touristSpotMapper::toListDTO);
    }

    public Page<TouristSpotListDTO> getTouristSpotsFromState(String stateName, Pageable pageable){
        return touristSpotRepository.findAllByStateName(stateName, pageable).map(touristSpotMapper::toListDTO);
    }

    public Page<TouristSpotListDTO> getSpotManagerTouristSpots(UUID spotManagerId, Pageable pageable){
        return touristSpotRepository
                .findAllBySpotManager_SpotManagerId(spotManagerId, pageable)
                .map(touristSpotMapper::toListDTO);
    }

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
}