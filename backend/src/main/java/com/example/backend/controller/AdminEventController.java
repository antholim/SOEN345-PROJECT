package com.example.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.service.AdminEventService;

@RestController
@RequestMapping("/api/admin/events")
public class AdminEventController {

	private final AdminEventService adminEventService;

	public AdminEventController(AdminEventService adminEventService) {
		this.adminEventService = adminEventService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EventResponse createEvent(@RequestBody CreateEventRequest request) {
		return adminEventService.createEvent(request);
	}

	@PutMapping("/{id}")
	public EventResponse updateEvent(@PathVariable int id, @RequestBody CreateEventRequest request) {
		return adminEventService.updateEvent(id, request);
	}

	@PatchMapping("/{id}/cancel")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancelEvent(@PathVariable int id) {
		adminEventService.cancelEvent(id);
	}
}
