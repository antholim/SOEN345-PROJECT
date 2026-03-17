package com.example.backend.auth;

public record LoginRequest(
	String email,
	String password
) {
}
