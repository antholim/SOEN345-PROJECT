package com.example.backend.notification;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Component
@ConditionalOnProperty(name = "notification.email.provider", havingValue = "sendgrid")
public class SendGridEmailProvider implements EmailProvider {
	private static final Logger log = LoggerFactory.getLogger(SendGridEmailProvider.class);

	private final String apiKey;
	private final String fromEmail;

	public SendGridEmailProvider(@Value("${notification.email.sendgrid.api-key:}") String apiKey,
								 @Value("${notification.email.sendgrid.from-email:}") String fromEmail) {
		this.apiKey = apiKey;
		this.fromEmail = fromEmail;
	}

	@Override
	public void sendEmail(String to, String subject, String body) {
		if (apiKey.isEmpty()) {
			log.warn("SendGrid API key is missing. Skipping email.");
			return;
		}

		Email from = new Email(fromEmail);
		Email toEmail = new Email(to);
		Content content = new Content("text/plain", body);
		Mail mail = new Mail(from, subject, toEmail, content);

		SendGrid sg = new SendGrid(apiKey);
		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			log.info("SendGrid response status code: {}", response.getStatusCode());
		} catch (IOException ex) {
			log.error("Error sending email via SendGrid", ex);
		}
	}
}
