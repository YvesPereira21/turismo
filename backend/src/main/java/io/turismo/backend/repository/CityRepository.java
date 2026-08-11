package io.turismo.backend.repository;

import io.turismo.backend.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID> {
    List<City> findAllByState_Name(String stateName);
    Optional<City> findByNameAndState_Name(String name, String stateName);
    boolean existsByNameAndState_Name(String cityName, String name);
}
