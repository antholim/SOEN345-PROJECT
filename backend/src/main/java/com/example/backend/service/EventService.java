package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.controller.EventResponse;
import com.example.backend.model.EventRepository;
import com.example.backend.model.EventStatus;

@Service
public class EventService {

	private final EventRepository eventRepository;

	public EventService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@Transactional(readOnly = true)
	public List<EventResponse> getActiveEvents() {
		return eventRepository.findByStatusOrderByEventDateAsc(EventStatus.ACTIVE)
			.stream()
			.map(e -> new EventResponse(
				e.getEventId(),
				e.getTitle(),
				e.getDescription(),
				e.getEventDate(),
				e.getAvailableTickets(),
				e.getPrice(),
				e.getStatus().name(),
				e.getVenue().getVenueName(),
				e.getVenue().getCity(),
				e.getCategory().getCategoryName()
			))
			.toList();
	}
}
