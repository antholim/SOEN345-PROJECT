package com.example.backend.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "notification.sms.provider", havingValue = "logger", matchIfMissing = true)
public class LoggerSmsProvider implements SmsProvider {
	private static final Logger log = LoggerFactory.getLogger(LoggerSmsProvider.class);

	@Override
	public void sendSms(String to, String message) {
		log.info("Sending SMS to {}: [Message: {}]", to, message);
	}
}
