package com.example.backend.controller;

public record LoginRequest(
	String email,
	String password
) {
}
