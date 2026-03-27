package com.example.backend.service;

import com.example.backend.model.ReservationEntity;

public interface ConfirmationService {
	void sendReservationConfirmation(ReservationEntity reservation);
	void sendCancellationConfirmation(ReservationEntity reservation);
}
