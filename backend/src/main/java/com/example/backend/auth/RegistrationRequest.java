package com.example.backend.auth;

public record RegistrationRequest(
	String firstName,
	String lastName,
	String email,
	String phoneNumber,
	String password
) {
}
