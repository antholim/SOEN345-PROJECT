package com.example.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.model.UserRole;
import com.example.backend.service.AuthService;

@WebMvcTest(AuthController.class)
class AuthControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;

	@Test
	void loginReturns200WithUserResponse() throws Exception {
		when(authService.login(any(LoginRequest.class))).thenReturn(
			new AuthUserResponse(1, "Eli", "Grant", "eli@example.com", null, UserRole.CUSTOMER, LocalDateTime.of(2026, 3, 16, 19, 0))
		);

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"email": "eli@example.com", "password": "password123"}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(1))
			.andExpect(jsonPath("$.email").value("eli@example.com"))
			.andExpect(jsonPath("$.role").value("CUSTOMER"));
	}

	@Test
	void loginPropagates401FromService() throws Exception {
		when(authService.login(any(LoginRequest.class)))
			.thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"email": "bad@example.com", "password": "wrong"}
					"""))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void registerReturns201WithUserResponse() throws Exception {
		when(authService.register(any(RegistrationRequest.class))).thenReturn(
			new AuthUserResponse(10, "Nina", "Sharp", "nina@example.com", null, UserRole.CUSTOMER, LocalDateTime.of(2026, 3, 16, 19, 0))
		);

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"firstName": "Nina", "lastName": "Sharp", "email": "nina@example.com", "password": "password123"}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.userId").value(10))
			.andExpect(jsonPath("$.firstName").value("Nina"))
			.andExpect(jsonPath("$.email").value("nina@example.com"));
	}

	@Test
	void registerPropagates400FromService() throws Exception {
		when(authService.register(any(RegistrationRequest.class)))
			.thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required"));

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"firstName": "Jo", "lastName": "Doe", "password": "password123"}
					"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	void registerPropagates409FromService() throws Exception {
		when(authService.register(any(RegistrationRequest.class)))
			.thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists"));

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"firstName": "Jo", "lastName": "Doe", "email": "taken@example.com", "password": "password123"}
					"""))
			.andExpect(status().isConflict());
	}
}
