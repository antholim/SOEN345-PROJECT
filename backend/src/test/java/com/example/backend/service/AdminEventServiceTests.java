package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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

@ExtendWith(MockitoExtension.class)
class AdminEventServiceTests {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private VenueRepository venueRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private AdminEventService adminEventService;

	@Captor
	private ArgumentCaptor<EventEntity> eventCaptor;

	@Captor
	private ArgumentCaptor<VenueEntity> venueCaptor;

	private CreateEventRequest makeRequest() {
		return new CreateEventRequest(
			"Rock Night",
			"An amazing rock concert",
			"2026-06-15T20:00:00",
			200,
			49.99,
			"Bell Centre",
			"Montreal",
			"1909 Av. des Canadiens-de-Montréal",
			21302,
			"Concert",
			1
		);
	}

	private VenueEntity makeVenue() {
		VenueEntity venue = new VenueEntity();
		venue.setVenueId(1);
		venue.setVenueName("Bell Centre");
		venue.setCity("Montreal");
		venue.setAddress("1909 Av. des Canadiens-de-Montréal");
		venue.setCapacity(21302);
		return venue;
	}

	private CategoryEntity makeCategory() {
		CategoryEntity category = new CategoryEntity();
		category.setCategoryId(1);
		category.setCategoryName("Concert");
		return category;
	}

	private EventEntity savedEvent(CreateEventRequest req, VenueEntity venue, CategoryEntity category) {
		EventEntity e = new EventEntity();
		e.setEventId(10);
		e.setTitle(req.title());
		e.setDescription(req.description());
		e.setEventDate(LocalDateTime.parse(req.eventDate()));
		e.setAvailableTickets(req.availableTickets());
		e.setPrice(BigDecimal.valueOf(req.price()));
		e.setStatus(EventStatus.ACTIVE);
		e.setVenue(venue);
		e.setCategory(category);
		e.setCreatedAt(LocalDateTime.now());
		e.setCreatedBy(req.createdBy());
		return e;
	}

	@Test
	void createEventReturnsCorrectResponse() {
		CreateEventRequest req = makeRequest();
		VenueEntity venue = makeVenue();
		CategoryEntity category = makeCategory();

		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.of(venue));
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.of(category));
		when(eventRepository.save(any(EventEntity.class))).thenReturn(savedEvent(req, venue, category));

		EventResponse response = adminEventService.createEvent(req);

		assertEquals(10, response.eventId());
		assertEquals("Rock Night", response.title());
		assertEquals("Bell Centre", response.venueName());
		assertEquals("Montreal", response.venueCity());
		assertEquals("Concert", response.categoryName());
		assertEquals("ACTIVE", response.status());
	}

	@Test
	void createEventSavesEventWithCorrectFields() {
		CreateEventRequest req = makeRequest();
		VenueEntity venue = makeVenue();
		CategoryEntity category = makeCategory();

		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.of(venue));
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.of(category));
		when(eventRepository.save(any(EventEntity.class))).thenAnswer(inv -> {
			EventEntity e = inv.getArgument(0, EventEntity.class);
			e.setEventId(10);
			return e;
		});

		adminEventService.createEvent(req);

		verify(eventRepository).save(eventCaptor.capture());
		EventEntity saved = eventCaptor.getValue();
		assertEquals("Rock Night", saved.getTitle());
		assertEquals(200, saved.getAvailableTickets());
		assertEquals(0, BigDecimal.valueOf(49.99).compareTo(saved.getPrice()));
		assertEquals(EventStatus.ACTIVE, saved.getStatus());
		assertEquals(1, saved.getCreatedBy());
		assertEquals(venue, saved.getVenue());
		assertEquals(category, saved.getCategory());
	}

	@Test
	void createEventUsesExistingVenueWhenFound() {
		CreateEventRequest req = makeRequest();
		VenueEntity existingVenue = makeVenue();
		CategoryEntity category = makeCategory();

		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.of(existingVenue));
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.of(category));
		when(eventRepository.save(any())).thenReturn(savedEvent(req, existingVenue, category));

		adminEventService.createEvent(req);

		verify(venueRepository, never()).save(any());
	}

	@Test
	void createEventCreatesNewVenueWhenNotFound() {
		CreateEventRequest req = makeRequest();
		VenueEntity newVenue = makeVenue();
		CategoryEntity category = makeCategory();

		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.empty());
		when(venueRepository.save(any(VenueEntity.class))).thenReturn(newVenue);
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.of(category));
		when(eventRepository.save(any())).thenReturn(savedEvent(req, newVenue, category));

		adminEventService.createEvent(req);

		verify(venueRepository).save(venueCaptor.capture());
		VenueEntity created = venueCaptor.getValue();
		assertEquals("Bell Centre", created.getVenueName());
		assertEquals("Montreal", created.getCity());
		assertEquals("1909 Av. des Canadiens-de-Montréal", created.getAddress());
		assertEquals(21302, created.getCapacity());
	}

	@Test
	void createEventThrowsBadRequestWhenCategoryNotFound() {
		CreateEventRequest req = makeRequest();
		VenueEntity venue = makeVenue();

		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.of(venue));
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> adminEventService.createEvent(req)
		);

		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
		verify(eventRepository, never()).save(any());
	}

	@Test
	void createEventSetsActiveStatus() {
		CreateEventRequest req = makeRequest();
		VenueEntity venue = makeVenue();
		CategoryEntity category = makeCategory();

		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.of(venue));
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.of(category));
		when(eventRepository.save(any(EventEntity.class))).thenAnswer(inv -> {
			EventEntity e = inv.getArgument(0, EventEntity.class);
			e.setEventId(10);
			return e;
		});

		adminEventService.createEvent(req);

		verify(eventRepository).save(eventCaptor.capture());
		assertEquals(EventStatus.ACTIVE, eventCaptor.getValue().getStatus());
	}

	// ── updateEvent ───────────────────────────────────────────────────────────

	private EventEntity existingEvent() {
		EventEntity e = new EventEntity();
		e.setEventId(10);
		e.setTitle("Old Title");
		e.setDescription("Old description");
		e.setEventDate(LocalDateTime.of(2026, 1, 1, 18, 0));
		e.setAvailableTickets(50);
		e.setPrice(new BigDecimal("25.00"));
		e.setStatus(EventStatus.ACTIVE);
		e.setVenue(makeVenue());
		e.setCategory(makeCategory());
		e.setCreatedBy(1);
		return e;
	}

	@Test
	void updateEventReturnsResponseWithNewValues() {
		CreateEventRequest req = makeRequest();
		VenueEntity venue = makeVenue();
		CategoryEntity category = makeCategory();
		EventEntity existing = existingEvent();

		when(eventRepository.findById(10)).thenReturn(Optional.of(existing));
		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.of(venue));
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.of(category));
		when(eventRepository.save(any(EventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

		EventResponse response = adminEventService.updateEvent(10, req);

		assertEquals("Rock Night", response.title());
		assertEquals(200, response.availableTickets());
		assertEquals(0, BigDecimal.valueOf(49.99).compareTo(response.price()));
	}

	@Test
	void updateEventSavesUpdatedFieldsOnExistingEntity() {
		CreateEventRequest req = makeRequest();
		VenueEntity venue = makeVenue();
		CategoryEntity category = makeCategory();
		EventEntity existing = existingEvent();

		when(eventRepository.findById(10)).thenReturn(Optional.of(existing));
		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.of(venue));
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.of(category));
		when(eventRepository.save(any(EventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

		adminEventService.updateEvent(10, req);

		verify(eventRepository).save(eventCaptor.capture());
		EventEntity saved = eventCaptor.getValue();
		assertEquals(10, saved.getEventId());
		assertEquals("Rock Night", saved.getTitle());
		assertEquals(200, saved.getAvailableTickets());
		assertEquals(0, BigDecimal.valueOf(49.99).compareTo(saved.getPrice()));
	}

	@Test
	void updateEventPreservesCreatedBy() {
		CreateEventRequest req = makeRequest();
		VenueEntity venue = makeVenue();
		CategoryEntity category = makeCategory();
		EventEntity existing = existingEvent();
		existing.setCreatedBy(42);

		when(eventRepository.findById(10)).thenReturn(Optional.of(existing));
		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.of(venue));
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.of(category));
		when(eventRepository.save(any(EventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

		adminEventService.updateEvent(10, req);

		verify(eventRepository).save(eventCaptor.capture());
		assertEquals(42, eventCaptor.getValue().getCreatedBy());
	}

	@Test
	void updateEventUsesExistingVenueWhenFound() {
		CreateEventRequest req = makeRequest();
		VenueEntity venue = makeVenue();
		CategoryEntity category = makeCategory();

		when(eventRepository.findById(10)).thenReturn(Optional.of(existingEvent()));
		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.of(venue));
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.of(category));
		when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		adminEventService.updateEvent(10, req);

		verify(venueRepository, never()).save(any());
	}

	@Test
	void updateEventCreatesNewVenueWhenNotFound() {
		CreateEventRequest req = makeRequest();
		VenueEntity newVenue = makeVenue();
		CategoryEntity category = makeCategory();

		when(eventRepository.findById(10)).thenReturn(Optional.of(existingEvent()));
		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.empty());
		when(venueRepository.save(any(VenueEntity.class))).thenReturn(newVenue);
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.of(category));
		when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		adminEventService.updateEvent(10, req);

		verify(venueRepository).save(venueCaptor.capture());
		assertEquals("Bell Centre", venueCaptor.getValue().getVenueName());
	}

	@Test
	void updateEventThrowsNotFoundWhenEventMissing() {
		when(eventRepository.findById(99)).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> adminEventService.updateEvent(99, makeRequest())
		);

		assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
		verify(eventRepository, never()).save(any());
	}

	@Test
	void updateEventThrowsBadRequestWhenCategoryNotFound() {
		when(eventRepository.findById(10)).thenReturn(Optional.of(existingEvent()));
		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.of(makeVenue()));
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> adminEventService.updateEvent(10, makeRequest())
		);

		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
		verify(eventRepository, never()).save(any());
	}

	// ── cancelEvent ───────────────────────────────────────────────────────────

	@Test
	void cancelEventSetsStatusToCancelled() {
		EventEntity event = new EventEntity();
		event.setEventId(1);
		event.setStatus(EventStatus.ACTIVE);

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(eventRepository.save(any(EventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

		adminEventService.cancelEvent(1);

		verify(eventRepository).save(eventCaptor.capture());
		assertEquals(EventStatus.CANCELLED, eventCaptor.getValue().getStatus());
	}

	@Test
	void cancelEventThrowsNotFoundWhenEventMissing() {
		when(eventRepository.findById(99)).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> adminEventService.cancelEvent(99)
		);

		assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
		verify(eventRepository, never()).save(any());
	}

	@Test
	void cancelEventThrowsConflictWhenAlreadyCancelled() {
		EventEntity event = new EventEntity();
		event.setEventId(1);
		event.setStatus(EventStatus.CANCELLED);

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> adminEventService.cancelEvent(1)
		);

		assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
		verify(eventRepository, never()).save(any());
	}

	@Test
	void createEventSetsCreatedAtTimestamp() {
		CreateEventRequest req = makeRequest();
		VenueEntity venue = makeVenue();
		CategoryEntity category = makeCategory();

		when(venueRepository.findByVenueNameAndCity("Bell Centre", "Montreal")).thenReturn(Optional.of(venue));
		when(categoryRepository.findByCategoryName("Concert")).thenReturn(Optional.of(category));
		when(eventRepository.save(any(EventEntity.class))).thenAnswer(inv -> {
			EventEntity e = inv.getArgument(0, EventEntity.class);
			e.setEventId(10);
			return e;
		});

		adminEventService.createEvent(req);

		verify(eventRepository).save(eventCaptor.capture());
		assertEquals(req.createdBy(), eventCaptor.getValue().getCreatedBy());
	}
}
