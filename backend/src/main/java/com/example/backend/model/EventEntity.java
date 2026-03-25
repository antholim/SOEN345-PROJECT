package com.example.backend.model;

import java.math.BigDecimal;
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
@Table(name = "events")
public class EventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private Integer eventId;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(columnDefinition = "text")
	private String description;

	@Column(name = "event_date", nullable = false)
	private LocalDateTime eventDate;

	@Column(name = "available_tickets", nullable = false)
	private Integer availableTickets;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(nullable = false)
	private EventStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venue_id", nullable = false)
	private VenueEntity venue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private CategoryEntity category;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public Integer getEventId() { return eventId; }
	public void setEventId(Integer eventId) { this.eventId = eventId; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public LocalDateTime getEventDate() { return eventDate; }
	public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

	public Integer getAvailableTickets() { return availableTickets; }
	public void setAvailableTickets(Integer availableTickets) { this.availableTickets = availableTickets; }

	public BigDecimal getPrice() { return price; }
	public void setPrice(BigDecimal price) { this.price = price; }

	public EventStatus getStatus() { return status; }
	public void setStatus(EventStatus status) { this.status = status; }

	public VenueEntity getVenue() { return venue; }
	public void setVenue(VenueEntity venue) { this.venue = venue; }

	public CategoryEntity getCategory() { return category; }
	public void setCategory(CategoryEntity category) { this.category = category; }

	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
