package com.example.backend.notification;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LoggerProviderTests {

	@Test
	void emailLoggerDoesNotThrow() {
		LoggerEmailProvider provider = new LoggerEmailProvider();
		assertDoesNotThrow(() -> provider.sendEmail("test@example.com", "Subject", "Body"));
	}

	@Test
	void smsLoggerDoesNotThrow() {
		LoggerSmsProvider provider = new LoggerSmsProvider();
		assertDoesNotThrow(() -> provider.sendSms("1234567890", "Message"));
	}
}
