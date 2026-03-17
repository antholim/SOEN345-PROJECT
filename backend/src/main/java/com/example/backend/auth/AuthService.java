package com.example.backend.auth;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.user.UserEntity;
import com.example.backend.user.UserRepository;
import com.example.backend.user.UserRole;

@Service
public class AuthService {

	private static final Pattern EMAIL_PATTERN =
		Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	private static final Pattern PHONE_PATTERN =
		Pattern.compile("^[0-9+()\\-\\s]{7,20}$");

	private final UserRepository userRepository;
	private final PasswordHasher passwordHasher;

	public AuthService(UserRepository userRepository, PasswordHasher passwordHasher) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
	}

	public AuthUserResponse login(LoginRequest request) {
		String identifier = request.email() == null ? "" : request.email().trim();
		String password = request.password() == null ? "" : request.password();

		if (identifier.isBlank() || password.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or phone number and password are required.");
		}

		UserEntity user = findByIdentifier(identifier)
			.orElseThrow(() -> invalidCredentials());

		if (!passwordHasher.matches(password, user.getPasswordHash())) {
			throw invalidCredentials();
		}

		return toResponse(user);
	}

	public AuthUserResponse register(RegistrationRequest request) {
		String firstName = normalizeRequired(request.firstName(), "First name");
		String lastName = normalizeRequired(request.lastName(), "Last name");
		String email = normalizeOptional(request.email());
		String phoneNumber = normalizeOptional(request.phoneNumber());
		String password = request.password() == null ? "" : request.password().trim();

		if (email == null && phoneNumber == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or phone number is required.");
		}

		if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid email address.");
		}

		if (phoneNumber != null && !PHONE_PATTERN.matcher(phoneNumber).matches()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid phone number.");
		}

		if (password.length() < 8) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters long.");
		}

		if (email != null && userRepository.existsByEmailIgnoreCase(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with that email already exists.");
		}

		if (phoneNumber != null && userRepository.existsByPhoneNumber(phoneNumber)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with that phone number already exists.");
		}

		UserEntity user = new UserEntity();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setPhoneNumber(phoneNumber);
		user.setPasswordHash(passwordHasher.hash(password));
		user.setRole(UserRole.CUSTOMER);
		user.setCreatedAt(LocalDateTime.now());

		return toResponse(userRepository.save(user));
	}

	private static ResponseStatusException invalidCredentials() {
		return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
	}

	private Optional<UserEntity> findByIdentifier(String identifier) {
		Optional<UserEntity> byEmail = userRepository.findByEmailIgnoreCase(identifier);
		if (byEmail.isPresent()) {
			return byEmail;
		}
		return userRepository.findByPhoneNumber(identifier);
	}

	private static String normalizeRequired(String value, String fieldName) {
		String normalized = normalizeOptional(value);
		if (normalized == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required.");
		}
		return normalized;
	}

	private static String normalizeOptional(String value) {
		if (value == null) {
			return null;
		}
		String normalized = value.trim();
		return normalized.isEmpty() ? null : normalized;
	}

	private static AuthUserResponse toResponse(UserEntity user) {
		return new AuthUserResponse(
			user.getUserId(),
			user.getFirstName(),
			user.getLastName(),
			user.getEmail(),
			user.getPhoneNumber(),
			user.getRole(),
			user.getCreatedAt()
		);
	}
}
