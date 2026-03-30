package com.example.backend.notification;

public interface SmsProvider {
	void sendSms(String to, String message);
}
