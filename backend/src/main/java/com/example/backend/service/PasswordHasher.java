package com.example.backend.service;

public interface PasswordHasher {

	String hash(String rawPassword);

	boolean matches(String rawPassword, String hashedPassword);
}
