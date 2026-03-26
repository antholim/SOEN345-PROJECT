package com.example.backend.controller;

import java.math.BigDecimal;

public record UserReservationResponse(
	Integer reservationId,
	String eventTitle,
	String eventDate,
	String venueCity,
	String venueName,
	String categoryName,
	Integer numberOfTickets,
	BigDecimal totalPrice,
	String status
) {
}
