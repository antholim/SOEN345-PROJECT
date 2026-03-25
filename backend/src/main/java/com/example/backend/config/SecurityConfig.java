package com.example.backend.config;

import com.example.backend.service.PasswordHasher;
import com.example.backend.service.Pbkdf2PasswordHasher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordHasher passwordHasher() {
		return new Pbkdf2PasswordHasher();
	}
}
