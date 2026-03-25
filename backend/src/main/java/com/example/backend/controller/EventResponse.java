package com.example.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
	Integer eventId,
	String title,
	String description,
	LocalDateTime eventDate,
	Integer availableTickets,
	BigDecimal price,
	String status,
	String venueName,
	String venueCity,
	String categoryName
) {
}
