package com.example.backend.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {

	List<EventEntity> findByStatusOrderByEventDateAsc(EventStatus status);
}
