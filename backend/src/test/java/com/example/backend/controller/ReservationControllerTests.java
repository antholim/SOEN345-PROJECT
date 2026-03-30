package com.example.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.service.ReservationService;

@WebMvcTest(ReservationController.class)
class ReservationControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReservationService reservationService;

	@Test
	void createReservationReturns201() throws Exception {
		when(reservationService.createReservation(any(ReservationRequest.class)))
			.thenReturn(new ReservationResponse(10, "Test Concert", 3, new BigDecimal("150.00"), "CONFIRMED"));

		mockMvc.perform(post("/api/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"userId": 2, "eventId": 1, "numberOfTickets": 3}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.reservationId").value(10))
			.andExpect(jsonPath("$.eventTitle").value("Test Concert"))
			.andExpect(jsonPath("$.numberOfTickets").value(3))
			.andExpect(jsonPath("$.status").value("CONFIRMED"));
	}

	@Test
	void getReservationsForUserReturns200() throws Exception {
		List<UserReservationResponse> reservations = List.of(
			new UserReservationResponse(10, "Test Concert", "2026-06-01T20:00", "Montreal",
				"Bell Centre", "Music", 2, new BigDecimal("100.00"), "CONFIRMED", "ACTIVE")
		);
		when(reservationService.getReservationsForUser(2)).thenReturn(reservations);

		mockMvc.perform(get("/api/reservations").param("userId", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].reservationId").value(10))
			.andExpect(jsonPath("$[0].eventTitle").value("Test Concert"))
			.andExpect(jsonPath("$[0].venueName").value("Bell Centre"));
	}

	@Test
	void cancelReservationReturns204() throws Exception {
		doNothing().when(reservationService).cancelReservation(10);

		mockMvc.perform(delete("/api/reservations/10"))
			.andExpect(status().isNoContent());
	}

	@Test
	void createReservationPropagates404WhenEventMissing() throws Exception {
		when(reservationService.createReservation(any(ReservationRequest.class)))
			.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

		mockMvc.perform(post("/api/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"userId": 2, "eventId": 99, "numberOfTickets": 1}
					"""))
			.andExpect(status().isNotFound());
	}

	@Test
	void createReservationPropagates409WhenNotEnoughTickets() throws Exception {
		when(reservationService.createReservation(any(ReservationRequest.class)))
			.thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Not enough tickets"));

		mockMvc.perform(post("/api/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"userId": 2, "eventId": 1, "numberOfTickets": 999}
					"""))
			.andExpect(status().isConflict());
	}

	@Test
	void cancelReservationPropagates404() throws Exception {
		doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"))
			.when(reservationService).cancelReservation(99);

		mockMvc.perform(delete("/api/reservations/99"))
			.andExpect(status().isNotFound());
	}
}
