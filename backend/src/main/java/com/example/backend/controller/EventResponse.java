package com.example.backend.controller;

import java.math.BigDecimal;

public record EventResponse(
	Integer eventId,
	String title,
	String description,
	String eventDate,
	Integer availableTickets,
	BigDecimal price,
	String status,
	String venueName,
	String venueCity,
	String venueAddress,
	Integer venueCapacity,
	String categoryName
) {
}
