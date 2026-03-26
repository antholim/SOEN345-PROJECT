package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

import com.example.backend.controller.ReservationRequest;
import com.example.backend.controller.ReservationResponse;
import com.example.backend.controller.UserReservationResponse;
import com.example.backend.model.CategoryEntity;
import com.example.backend.model.EventEntity;
import com.example.backend.model.EventRepository;
import com.example.backend.model.EventStatus;
import com.example.backend.model.ReservationEntity;
import com.example.backend.model.ReservationRepository;
import com.example.backend.model.ReservationStatus;
import com.example.backend.model.UserEntity;
import com.example.backend.model.UserRepository;
import com.example.backend.model.UserRole;
import com.example.backend.model.VenueEntity;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTests {

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private EventRepository eventRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private ReservationService reservationService;

	@Captor
	private ArgumentCaptor<ReservationEntity> reservationCaptor;

	@Captor
	private ArgumentCaptor<EventEntity> eventCaptor;

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
		category.setCategoryName("Music");
		return category;
	}

	private EventEntity makeEvent(int id, int availableTickets) {
		EventEntity event = new EventEntity();
		event.setEventId(id);
		event.setTitle("Test Concert");
		event.setAvailableTickets(availableTickets);
		event.setPrice(new BigDecimal("50.00"));
		event.setStatus(EventStatus.ACTIVE);
		event.setEventDate(LocalDateTime.of(2026, 6, 1, 20, 0));
		event.setVenue(makeVenue());
		event.setCategory(makeCategory());
		return event;
	}

	private ReservationEntity makeReservation(int id, ReservationStatus status, EventEntity event, UserEntity user, int tickets) {
		ReservationEntity r = new ReservationEntity();
		r.setReservationId(id);
		r.setStatus(status);
		r.setEvent(event);
		r.setUser(user);
		r.setNumberOfTickets(tickets);
		r.setTotalPrice(event.getPrice().multiply(BigDecimal.valueOf(tickets)));
		r.setReservationDate(LocalDateTime.now());
		return r;
	}

	private UserEntity makeUser(int id) {
		UserEntity user = new UserEntity();
		user.setUserId(id);
		user.setFirstName("Jane");
		user.setLastName("Doe");
		user.setEmail("jane@example.com");
		user.setRole(UserRole.CUSTOMER);
		user.setCreatedAt(LocalDateTime.now());
		return user;
	}

	@Test
	void createReservationReturnsConfirmedReservation() {
		EventEntity event = makeEvent(1, 100);
		UserEntity user = makeUser(2);

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(userRepository.findById(2)).thenReturn(Optional.of(user));
		when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(inv -> {
			ReservationEntity r = inv.getArgument(0, ReservationEntity.class);
			r.setReservationId(10);
			return r;
		});

		ReservationResponse response = reservationService.createReservation(
			new ReservationRequest(2, 1, 3)
		);

		assertEquals(10, response.reservationId());
		assertEquals("Test Concert", response.eventTitle());
		assertEquals(3, response.numberOfTickets());
		assertEquals(new BigDecimal("150.00"), response.totalPrice());
		assertEquals("CONFIRMED", response.status());
	}

	@Test
	void createReservationDecrementsAvailableTickets() {
		EventEntity event = makeEvent(1, 100);
		UserEntity user = makeUser(2);

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(userRepository.findById(2)).thenReturn(Optional.of(user));
		when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(inv -> inv.getArgument(0));

		reservationService.createReservation(new ReservationRequest(2, 1, 4));

		verify(eventRepository).save(eventCaptor.capture());
		assertEquals(96, eventCaptor.getValue().getAvailableTickets());
	}

	@Test
	void createReservationThrowsNotFoundWhenEventMissing() {
		when(eventRepository.findById(99)).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> reservationService.createReservation(new ReservationRequest(2, 99, 1))
		);

		assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
		verify(reservationRepository, never()).save(any());
	}

	@Test
	void createReservationThrowsConflictWhenNotEnoughTickets() {
		EventEntity event = makeEvent(1, 2);
		when(eventRepository.findById(1)).thenReturn(Optional.of(event));

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> reservationService.createReservation(new ReservationRequest(2, 1, 5))
		);

		assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
		verify(reservationRepository, never()).save(any());
	}

	@Test
	void createReservationThrowsNotFoundWhenUserMissing() {
		EventEntity event = makeEvent(1, 100);
		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(userRepository.findById(99)).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> reservationService.createReservation(new ReservationRequest(99, 1, 1))
		);

		assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
		verify(reservationRepository, never()).save(any());
	}

	// ── cancelReservation ──────────────────────────────────────────────────────

	@Test
	void cancelReservationSetsCancelledStatusAndRestoresTickets() {
		EventEntity event = makeEvent(1, 50);
		UserEntity user = makeUser(2);
		ReservationEntity reservation = makeReservation(10, ReservationStatus.CONFIRMED, event, user, 3);

		when(reservationRepository.findById(10)).thenReturn(Optional.of(reservation));
		when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(inv -> inv.getArgument(0));

		reservationService.cancelReservation(10);

		verify(eventRepository).save(eventCaptor.capture());
		assertEquals(53, eventCaptor.getValue().getAvailableTickets());

		verify(reservationRepository).save(reservationCaptor.capture());
		assertEquals(ReservationStatus.CANCELLED, reservationCaptor.getValue().getStatus());
	}

	@Test
	void cancelReservationThrowsNotFoundWhenReservationMissing() {
		when(reservationRepository.findById(99)).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> reservationService.cancelReservation(99)
		);

		assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
		verify(reservationRepository, never()).save(any());
		verify(eventRepository, never()).save(any());
	}

	@Test
	void cancelReservationThrowsConflictWhenAlreadyCancelled() {
		EventEntity event = makeEvent(1, 50);
		UserEntity user = makeUser(2);
		ReservationEntity reservation = makeReservation(10, ReservationStatus.CANCELLED, event, user, 3);

		when(reservationRepository.findById(10)).thenReturn(Optional.of(reservation));

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> reservationService.cancelReservation(10)
		);

		assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
		verify(reservationRepository, never()).save(any());
		verify(eventRepository, never()).save(any());
	}

	// ── getReservationsForUser ──────────────────────────────────────────────────

	@Test
	void getReservationsForUserReturnsMappedList() {
		EventEntity event = makeEvent(1, 50);
		UserEntity user = makeUser(2);
		ReservationEntity reservation = makeReservation(10, ReservationStatus.CONFIRMED, event, user, 2);

		when(reservationRepository.findAllByUser_UserId(2)).thenReturn(List.of(reservation));

		List<UserReservationResponse> result = reservationService.getReservationsForUser(2);

		assertEquals(1, result.size());
		UserReservationResponse response = result.get(0);
		assertEquals(10, response.reservationId());
		assertEquals("Test Concert", response.eventTitle());
		assertEquals("Montreal", response.venueCity());
		assertEquals("Bell Centre", response.venueName());
		assertEquals("Music", response.categoryName());
		assertEquals(2, response.numberOfTickets());
		assertEquals(new BigDecimal("100.00"), response.totalPrice());
		assertEquals("CONFIRMED", response.status());
	}

	@Test
	void getReservationsForUserReturnsEmptyListWhenNoReservations() {
		when(reservationRepository.findAllByUser_UserId(2)).thenReturn(List.of());

		List<UserReservationResponse> result = reservationService.getReservationsForUser(2);

		assertTrue(result.isEmpty());
	}

	@Test
	void createReservationSavesReservationWithCorrectStatus() {
		EventEntity event = makeEvent(1, 100);
		UserEntity user = makeUser(2);

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(userRepository.findById(2)).thenReturn(Optional.of(user));
		when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(inv -> inv.getArgument(0));

		reservationService.createReservation(new ReservationRequest(2, 1, 2));

		verify(reservationRepository).save(reservationCaptor.capture());
		ReservationEntity saved = reservationCaptor.getValue();
		assertEquals(ReservationStatus.CONFIRMED, saved.getStatus());
		assertEquals(2, saved.getNumberOfTickets());
		assertEquals(new BigDecimal("100.00"), saved.getTotalPrice());
	}
}
