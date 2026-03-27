package com.example.backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.model.ConfirmationType;
import com.example.backend.model.EventEntity;
import com.example.backend.model.ReservationEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.model.VenueEntity;
import com.example.backend.notification.EmailProvider;
import com.example.backend.notification.SmsProvider;

@ExtendWith(MockitoExtension.class)
class ConfirmationServiceTests {

	@Mock
	private EmailProvider emailProvider;

	@Mock
	private SmsProvider smsProvider;

	@Mock
	private ConfirmationHistoryService historyService;

	@InjectMocks
	private DefaultConfirmationService confirmationService;

	private ReservationEntity makeReservation() {
		VenueEntity venue = new VenueEntity();
		venue.setVenueName("Bell Centre");
		venue.setCity("Montreal");

		EventEntity event = new EventEntity();
		event.setTitle("Test Concert");
		event.setEventDate(LocalDateTime.of(2026, 6, 1, 20, 0));
		event.setVenue(venue);

		UserEntity user = new UserEntity();
		user.setEmail("jane@example.com");
		user.setPhoneNumber("514-123-4567");

		ReservationEntity r = new ReservationEntity();
		r.setReservationId(10);
		r.setUser(user);
		r.setEvent(event);
		r.setNumberOfTickets(2);
		r.setTotalPrice(new BigDecimal("100.00"));
		return r;
	}

	@Test
	void sendReservationConfirmationSendsToBothEmailAndSms() {
		ReservationEntity r = makeReservation();

		confirmationService.sendReservationConfirmation(r);

		verify(emailProvider).sendEmail(eq("jane@example.com"), eq("Reservation Confirmation"), any());
		verify(smsProvider).sendSms(eq("514-123-4567"), any());
		verify(historyService, org.mockito.Mockito.times(2)).saveHistory(eq(r), any(ConfirmationType.class), any());
	}

	@Test
	void sendCancellationConfirmationSendsToBothEmailAndSms() {
		ReservationEntity r = makeReservation();

		confirmationService.sendCancellationConfirmation(r);

		verify(emailProvider).sendEmail(eq("jane@example.com"), eq("Reservation Confirmation"), any());
		verify(smsProvider).sendSms(eq("514-123-4567"), any());
		verify(historyService, org.mockito.Mockito.times(2)).saveHistory(eq(r), any(ConfirmationType.class), any());
	}
}
