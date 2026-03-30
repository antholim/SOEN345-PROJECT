package com.example.backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.model.ConfirmationEntity;
import com.example.backend.model.ConfirmationRepository;
import com.example.backend.model.ConfirmationType;
import com.example.backend.model.ReservationEntity;

@ExtendWith(MockitoExtension.class)
class ConfirmationHistoryServiceTests {

	@Mock
	private ConfirmationRepository confirmationRepository;

	@InjectMocks
	private ConfirmationHistoryService historyService;

	@Test
	void saveHistorySavesToRepository() {
		ReservationEntity r = new ReservationEntity();
		historyService.saveHistory(r, ConfirmationType.EMAIL, "Test Message");
		verify(confirmationRepository).save(any(ConfirmationEntity.class));
	}

	@Test
	void saveHistoryHandlesExceptionsSilently() {
		ReservationEntity r = new ReservationEntity();
		doThrow(new RuntimeException("DB Error")).when(confirmationRepository).save(any());
		
		// Should not throw exception
		historyService.saveHistory(r, ConfirmationType.EMAIL, "Test Message");
		
		verify(confirmationRepository).save(any());
	}
}
