package com.example.backend.controller;

import java.time.LocalDateTime;

import com.example.backend.model.UserRole;

public record AuthUserResponse(
	Integer userId,
	String firstName,
	String lastName,
	String email,
	String phoneNumber,
	UserRole role,
	LocalDateTime createdAt
) {
}
