package com.example.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.controller.CreateEventRequest;
import com.example.backend.controller.EventResponse;
import com.example.backend.model.CategoryEntity;
import com.example.backend.model.CategoryRepository;
import com.example.backend.model.EventEntity;
import com.example.backend.model.EventRepository;
import com.example.backend.model.EventStatus;
import com.example.backend.model.VenueEntity;
import com.example.backend.model.VenueRepository;

@Service
public class AdminEventService {

	private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private final EventRepository eventRepository;
	private final VenueRepository venueRepository;
	private final CategoryRepository categoryRepository;

	public AdminEventService(EventRepository eventRepository, VenueRepository venueRepository,
			CategoryRepository categoryRepository) {
		this.eventRepository = eventRepository;
		this.venueRepository = venueRepository;
		this.categoryRepository = categoryRepository;
	}

	@Transactional
	public void cancelEvent(int eventId) {
		EventEntity event = eventRepository.findById(eventId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + eventId));
		if (event.getStatus() == EventStatus.CANCELLED) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Event is already cancelled");
		}
		event.setStatus(EventStatus.CANCELLED);
		eventRepository.save(event);
	}

	@Transactional
	public EventResponse createEvent(CreateEventRequest req) {
		VenueEntity venue = venueRepository.findByVenueNameAndCity(req.venueName(), req.venueCity())
			.orElseGet(() -> {
				VenueEntity v = new VenueEntity();
				v.setVenueName(req.venueName());
				v.setCity(req.venueCity());
				v.setAddress(req.venueAddress());
				v.setCapacity(req.venueCapacity());
				return venueRepository.save(v);
			});

		CategoryEntity category = categoryRepository.findByCategoryName(req.categoryName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
				"Category not found: " + req.categoryName()));

		EventEntity event = new EventEntity();
		event.setTitle(req.title());
		event.setDescription(req.description());
		event.setEventDate(LocalDateTime.parse(req.eventDate()));
		event.setAvailableTickets(req.availableTickets());
		event.setPrice(BigDecimal.valueOf(req.price()));
		event.setStatus(EventStatus.ACTIVE);
		event.setVenue(venue);
		event.setCategory(category);
		event.setCreatedAt(LocalDateTime.now());
		event.setCreatedBy(req.createdBy());

		EventEntity saved = eventRepository.save(event);

		return toResponse(saved);
	}

	@Transactional
	public EventResponse updateEvent(int eventId, CreateEventRequest req) {
		EventEntity event = eventRepository.findById(eventId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + eventId));

		VenueEntity venue = venueRepository.findByVenueNameAndCity(req.venueName(), req.venueCity())
			.orElseGet(() -> {
				VenueEntity v = new VenueEntity();
				v.setVenueName(req.venueName());
				v.setCity(req.venueCity());
				v.setAddress(req.venueAddress());
				v.setCapacity(req.venueCapacity());
				return venueRepository.save(v);
			});

		CategoryEntity category = categoryRepository.findByCategoryName(req.categoryName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
				"Category not found: " + req.categoryName()));

		event.setTitle(req.title());
		event.setDescription(req.description());
		event.setEventDate(LocalDateTime.parse(req.eventDate()));
		event.setAvailableTickets(req.availableTickets());
		event.setPrice(BigDecimal.valueOf(req.price()));
		event.setVenue(venue);
		event.setCategory(category);

		return toResponse(eventRepository.save(event));
	}

	private EventResponse toResponse(EventEntity e) {
		return new EventResponse(
			e.getEventId(),
			e.getTitle(),
			e.getDescription(),
			e.getEventDate().format(ISO_FORMATTER),
			e.getAvailableTickets(),
			e.getPrice(),
			e.getStatus().name(),
			e.getVenue().getVenueName(),
			e.getVenue().getCity(),
			e.getVenue().getAddress(),
			e.getVenue().getCapacity(),
			e.getCategory().getCategoryName()
		);
	}
}
