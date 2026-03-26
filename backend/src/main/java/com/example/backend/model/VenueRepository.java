package com.example.backend.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<VenueEntity, Integer> {

	Optional<VenueEntity> findByVenueNameAndCity(String venueName, String city);
}
