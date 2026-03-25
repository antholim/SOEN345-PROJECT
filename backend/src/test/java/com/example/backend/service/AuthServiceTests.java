package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.controller.AuthUserResponse;
import com.example.backend.controller.LoginRequest;
import com.example.backend.controller.RegistrationRequest;
import com.example.backend.model.UserEntity;
import com.example.backend.model.UserRepository;
import com.example.backend.model.UserRole;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordHasher passwordHasher;

	@InjectMocks
	private AuthService authService;

	@Captor
	private ArgumentCaptor<UserEntity> userCaptor;

	@Test
	void registerWithEmailSavesUserInDatabase() {
		when(userRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
		when(passwordHasher.hash("password123")).thenReturn("hashed-password123");
		when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
			UserEntity user = invocation.getArgument(0, UserEntity.class);
			user.setUserId(10);
			return user;
		});

		AuthUserResponse response = authService.register(
			new RegistrationRequest("Nina", "Sharp", "new@example.com", null, "password123")
		);

		verify(userRepository).save(userCaptor.capture());
		UserEntity savedUser = userCaptor.getValue();

		assertEquals("Nina", savedUser.getFirstName());
		assertEquals("Sharp", savedUser.getLastName());
		assertEquals("new@example.com", savedUser.getEmail());
		assertNull(savedUser.getPhoneNumber());
		assertEquals(UserRole.CUSTOMER, savedUser.getRole());
		assertEquals("hashed-password123", savedUser.getPasswordHash());
		assertNotNull(savedUser.getCreatedAt());
		assertEquals(10, response.userId());
		assertEquals("new@example.com", response.email());
		assertEquals(UserRole.CUSTOMER, response.role());
	}

	@Test
	void registerWithPhoneOnlySavesUserInDatabase() {
		when(userRepository.existsByPhoneNumber("+1 514 555 0199")).thenReturn(false);
		when(passwordHasher.hash("password123")).thenReturn("hashed-password123");
		when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
			UserEntity user = invocation.getArgument(0, UserEntity.class);
			user.setUserId(11);
			return user;
		});

		AuthUserResponse response = authService.register(
			new RegistrationRequest("Sam", "Bell", null, "+1 514 555 0199", "password123")
		);

		verify(userRepository).save(userCaptor.capture());
		UserEntity savedUser = userCaptor.getValue();

		assertNull(savedUser.getEmail());
		assertEquals("+1 514 555 0199", savedUser.getPhoneNumber());
		assertEquals(11, response.userId());
		assertEquals("+1 514 555 0199", response.phoneNumber());
	}

	@Test
	void registerRejectsMissingEmailAndPhone() {
		ResponseStatusException exception = assertThrows(
			ResponseStatusException.class,
			() -> authService.register(new RegistrationRequest("Ava", "Cole", " ", " ", "password123"))
		);

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(userRepository, never()).save(any(UserEntity.class));
	}

	@Test
	void registerRejectsDuplicateEmail() {
		when(userRepository.existsByEmailIgnoreCase("taken@example.com")).thenReturn(true);

		ResponseStatusException exception = assertThrows(
			ResponseStatusException.class,
			() -> authService.register(
				new RegistrationRequest("Lena", "Hart", "taken@example.com", null, "password123")
			)
		);

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		verify(userRepository, never()).save(any(UserEntity.class));
	}

	@Test
	void loginAcceptsPhoneNumberIdentifier() {
		UserEntity user = new UserEntity();
		user.setUserId(20);
		user.setFirstName("Mira");
		user.setLastName("Stone");
		user.setEmail(null);
		user.setPhoneNumber("+1 438 555 0101");
		user.setPasswordHash("stored-hash");
		user.setRole(UserRole.CUSTOMER);
		user.setCreatedAt(LocalDateTime.of(2026, 3, 16, 19, 0));

		when(userRepository.findByEmailIgnoreCase("+1 438 555 0101")).thenReturn(Optional.empty());
		when(userRepository.findByPhoneNumber("+1 438 555 0101")).thenReturn(Optional.of(user));
		when(passwordHasher.matches("password123", "stored-hash")).thenReturn(true);

		AuthUserResponse response = authService.login(
			new LoginRequest("+1 438 555 0101", "password123")
		);

		assertEquals(20, response.userId());
		assertEquals("+1 438 555 0101", response.phoneNumber());
		assertNull(response.email());
	}
}
