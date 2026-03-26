package com.example.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

	private final ReservationService reservationService;

	public ReservationController(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ReservationResponse createReservation(@RequestBody ReservationRequest request) {
		return reservationService.createReservation(request);
	}

	@GetMapping
	public List<UserReservationResponse> getReservationsForUser(@RequestParam Integer userId) {
		return reservationService.getReservationsForUser(userId);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancelReservation(@PathVariable Integer id) {
		reservationService.cancelReservation(id);
	}
}
