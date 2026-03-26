package com.example.backend.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {
	List<ReservationEntity> findAllByUser_UserId(Integer userId);
}
