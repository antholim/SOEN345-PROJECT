package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.service.EventService;

@RestController
@RequestMapping("/api/events")
public class EventController {

	private final EventService eventService;

	public EventController(EventService eventService) {
		this.eventService = eventService;
	}

	@GetMapping
	public PagedEventResponse getActiveEvents(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) String city,
		@RequestParam(required = false) String category,
		@RequestParam(required = false) String fromDate
	) {
		return eventService.getActiveEvents(page, keyword, city, category, fromDate);
	}
}
