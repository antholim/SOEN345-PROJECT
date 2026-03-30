package com.example.backend.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.ConfirmationEntity;
import com.example.backend.model.ConfirmationRepository;
import com.example.backend.model.ConfirmationType;
import com.example.backend.model.ReservationEntity;
import com.example.backend.notification.EmailProvider;
import com.example.backend.notification.SmsProvider;

@Service
public class DefaultConfirmationService implements ConfirmationService {

	private final EmailProvider emailProvider;
	private final SmsProvider smsProvider;
	private final ConfirmationHistoryService historyService;

	public DefaultConfirmationService(EmailProvider emailProvider,
									  SmsProvider smsProvider,
									  ConfirmationHistoryService historyService) {
		this.emailProvider = emailProvider;
		this.smsProvider = smsProvider;
		this.historyService = historyService;
	}

	@Override
	@Transactional
	public void sendReservationConfirmation(ReservationEntity reservation) {
		String message = buildMessage(reservation, "CONFIRMED");
		send(reservation, message);
	}

	@Override
	@Transactional
	public void sendCancellationConfirmation(ReservationEntity reservation) {
		String message = buildMessage(reservation, "CANCELLED");
		send(reservation, message);
	}

	private String buildMessage(ReservationEntity r, String status) {
		var event = r.getEvent();
		var venue = event.getVenue();
		return String.format(
			"Reservation #%d %s\n" +
			"Event: %s\n" +
			"Date: %s\n" +
			"Venue: %s, %s\n" +
			"Tickets: %d\n" +
			"Total Price: $%s",
			r.getReservationId(),
			status,
			event.getTitle(),
			event.getEventDate().toString(),
			venue.getVenueName(),
			venue.getCity(),
			r.getNumberOfTickets(),
			r.getTotalPrice().toString()
		);
	}

	private void send(ReservationEntity reservation, String message) {
		var user = reservation.getUser();
		boolean sentAny = false;

		if (user.getEmail() != null && !user.getEmail().isBlank()) {
			emailProvider.sendEmail(user.getEmail(), "Reservation Confirmation", message);
			historyService.saveHistory(reservation, ConfirmationType.EMAIL, message);
			sentAny = true;
		}

		if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
			smsProvider.sendSms(user.getPhoneNumber(), message);
			historyService.saveHistory(reservation, ConfirmationType.SMS, message);
			sentAny = true;
		}

		if (!sentAny) {
			// Fallback or log if no contact info
		}
	}
}
