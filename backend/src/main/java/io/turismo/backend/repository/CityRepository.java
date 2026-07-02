package io.turismo.backend.repository;

import io.turismo.backend.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID> {
    Page<City> findAllByState_Name(String stateName, Pageable pageable);
    Optional<City> findByNameAndState_Name(String name, String stateName);
    boolean existsByNameAndState_Name(String cityName, String name);
}
