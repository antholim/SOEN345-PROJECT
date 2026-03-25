package com.example.backend.controller;

public record RegistrationRequest(
	String firstName,
	String lastName,
	String email,
	String phoneNumber,
	String password
) {
}
