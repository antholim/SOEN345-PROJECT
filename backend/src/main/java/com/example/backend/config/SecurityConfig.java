package com.example.backend.config;

import com.example.backend.auth.PasswordHasher;
import com.example.backend.auth.Pbkdf2PasswordHasher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordHasher passwordHasher() {
		return new Pbkdf2PasswordHasher();
	}
}
