package com.example.backend.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	public AuthUserResponse login(@RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthUserResponse register(@RequestBody RegistrationRequest request) {
		return authService.register(request);
	}
}
