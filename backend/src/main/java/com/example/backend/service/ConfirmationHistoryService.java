package com.example.backend.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.ConfirmationEntity;
import com.example.backend.model.ConfirmationRepository;
import com.example.backend.model.ConfirmationType;
import com.example.backend.model.ReservationEntity;

@Service
public class ConfirmationHistoryService {
	private static final Logger log = LoggerFactory.getLogger(ConfirmationHistoryService.class);
	private final ConfirmationRepository confirmationRepository;

	public ConfirmationHistoryService(ConfirmationRepository confirmationRepository) {
		this.confirmationRepository = confirmationRepository;
	}

	@org.springframework.scheduling.annotation.Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveHistory(ReservationEntity reservation, ConfirmationType type, String message) {
		try {
			ConfirmationEntity confirmation = new ConfirmationEntity();
			confirmation.setReservation(reservation);
			confirmation.setType(type);
			confirmation.setMessage(message);
			confirmation.setSentAt(LocalDateTime.now());
			confirmationRepository.save(confirmation);
		} catch (Exception ex) {
			log.error("Silent failure: Could not save confirmation to DB history. This is likely due to the UNIQUE constraint issue. Error: {}", ex.getMessage());
			// We do NOT rethrow. The transaction for this history record will rollback,
			// but the main reservation transaction will be unaffected.
		}
	}
}
