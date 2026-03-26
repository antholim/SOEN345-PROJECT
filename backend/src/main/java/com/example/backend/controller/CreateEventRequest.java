package com.example.backend.controller;

public record CreateEventRequest(
	String title,
	String description,
	String eventDate,
	int availableTickets,
	double price,
	String venueName,
	String venueCity,
	String venueAddress,
	int venueCapacity,
	String categoryName,
	int createdBy
) {
}
