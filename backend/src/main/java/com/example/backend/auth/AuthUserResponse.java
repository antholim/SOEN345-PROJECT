package com.example.backend.auth;

import java.time.LocalDateTime;

import com.example.backend.user.UserRole;

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
