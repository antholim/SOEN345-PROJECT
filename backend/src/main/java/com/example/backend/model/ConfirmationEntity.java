package com.example.backend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "confirmations")
public class ConfirmationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "confirmation_id")
	private Integer confirmationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reservation_id", nullable = false)
	private ReservationEntity reservation;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "confirmation_type", nullable = false)
	private ConfirmationType type;

	@Column(name = "sent_at", nullable = false)
	private LocalDateTime sentAt;

	@Column(name = "confirmation_message", columnDefinition = "text")
	private String message;

	public Integer getConfirmationId() { return confirmationId; }
	public void setConfirmationId(Integer confirmationId) { this.confirmationId = confirmationId; }

	public ReservationEntity getReservation() { return reservation; }
	public void setReservation(ReservationEntity reservation) { this.reservation = reservation; }

	public ConfirmationType getType() { return type; }
	public void setType(ConfirmationType type) { this.type = type; }

	public LocalDateTime getSentAt() { return sentAt; }
	public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
}
