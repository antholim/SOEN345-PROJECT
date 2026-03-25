package com.example.backend.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

	Optional<UserEntity> findByEmailIgnoreCase(String email);

	Optional<UserEntity> findByPhoneNumber(String phoneNumber);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByPhoneNumber(String phoneNumber);
}
