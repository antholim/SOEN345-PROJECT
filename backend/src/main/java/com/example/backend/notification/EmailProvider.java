package com.example.backend.notification;

public interface EmailProvider {
	void sendEmail(String to, String subject, String body);
}
