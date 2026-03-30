package com.example.backend.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "notification.email.provider", havingValue = "logger", matchIfMissing = true)
public class LoggerEmailProvider implements EmailProvider {
	private static final Logger log = LoggerFactory.getLogger(LoggerEmailProvider.class);

	@Override
	public void sendEmail(String to, String subject, String body) {
		log.info("Sending EMAIL to {}: [Subject: {}] [Body: {}]", to, subject, body);
	}
}
