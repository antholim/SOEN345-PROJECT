package com.example.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.controller.ReservationRequest;
import com.example.backend.controller.ReservationResponse;
import com.example.backend.controller.UserReservationResponse;
import com.example.backend.model.EventRepository;
import com.example.backend.model.ReservationEntity;
import com.example.backend.model.ReservationRepository;
import com.example.backend.model.ReservationStatus;
import com.example.backend.model.UserRepository;

@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;

	public ReservationService(ReservationRepository reservationRepository,
							  EventRepository eventRepository,
							  UserRepository userRepository) {
		this.reservationRepository = reservationRepository;
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public ReservationResponse createReservation(ReservationRequest request) {
		var event = eventRepository.findById(request.eventId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

		if (event.getAvailableTickets() < request.numberOfTickets()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough tickets available");
		}

		var user = userRepository.findById(request.userId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		event.setAvailableTickets(event.getAvailableTickets() - request.numberOfTickets());
		eventRepository.save(event);

		var reservation = new ReservationEntity();
		reservation.setUser(user);
		reservation.setEvent(event);
		reservation.setNumberOfTickets(request.numberOfTickets());
		reservation.setReservationDate(LocalDateTime.now());
		reservation.setStatus(ReservationStatus.CONFIRMED);
		reservation.setTotalPrice(event.getPrice().multiply(BigDecimal.valueOf(request.numberOfTickets())));

		var saved = reservationRepository.save(reservation);

		return new ReservationResponse(
			saved.getReservationId(),
			event.getTitle(),
			saved.getNumberOfTickets(),
			saved.getTotalPrice(),
			saved.getStatus().name()
		);
	}

	@Transactional
	public void cancelReservation(Integer reservationId) {
		var reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

		if (reservation.getStatus() == ReservationStatus.CANCELLED) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation is already cancelled");
		}

		var event = reservation.getEvent();
		event.setAvailableTickets(event.getAvailableTickets() + reservation.getNumberOfTickets());
		eventRepository.save(event);

		reservation.setStatus(ReservationStatus.CANCELLED);
		reservationRepository.save(reservation);
	}

	@Transactional(readOnly = true)
	public List<UserReservationResponse> getReservationsForUser(Integer userId) {
		return reservationRepository.findAllByUser_UserId(userId).stream()
			.map(r -> {
				var event = r.getEvent();
				var venue = event.getVenue();
				var category = event.getCategory();
				return new UserReservationResponse(
					r.getReservationId(),
					event.getTitle(),
					event.getEventDate().toString(),
					venue.getCity(),
					venue.getVenueName(),
					category.getCategoryName(),
					r.getNumberOfTickets(),
					r.getTotalPrice(),
					r.getStatus().name(),
					event.getStatus().name()
				);
			})
			.toList();
	}
}
