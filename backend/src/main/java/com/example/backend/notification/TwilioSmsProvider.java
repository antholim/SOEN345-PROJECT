package com.example.backend.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Component
@ConditionalOnProperty(name = "notification.sms.provider", havingValue = "twilio")
public class TwilioSmsProvider implements SmsProvider {
	private static final Logger log = LoggerFactory.getLogger(TwilioSmsProvider.class);

	private final String accountSid;
	private final String authToken;
	private final String fromPhone;

	public TwilioSmsProvider(@Value("${notification.sms.twilio.account-sid:}") String accountSid,
							 @Value("${notification.sms.twilio.auth-token:}") String authToken,
							 @Value("${notification.sms.twilio.from-phone:}") String fromPhone) {
		this.accountSid = accountSid;
		this.authToken = authToken;
		this.fromPhone = fromPhone;
		if (!accountSid.isEmpty() && !authToken.isEmpty()) {
			Twilio.init(accountSid, authToken);
		}
	}

	@Override
	public void sendSms(String to, String message) {
		if (accountSid.isEmpty() || authToken.isEmpty()) {
			log.warn("Twilio credentials missing. Skipping SMS.");
			return;
		}

		try {
			Message twilioMessage = Message.creator(
				new PhoneNumber(to),
				new PhoneNumber(fromPhone),
				message
			).create();
			log.info("Twilio SMS sent with SID: {}", twilioMessage.getSid());
		} catch (Exception ex) {
			log.error("Error sending SMS via Twilio", ex);
		}
	}
}
