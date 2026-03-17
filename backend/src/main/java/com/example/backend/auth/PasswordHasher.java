package com.example.backend.auth;

public interface PasswordHasher {

	String hash(String rawPassword);

	boolean matches(String rawPassword, String hashedPassword);
}
