package com.example.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.controller.EventResponse;
import com.example.backend.controller.PagedEventResponse;
import com.example.backend.model.EventRepository;
import com.example.backend.model.EventStatus;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

@Service
public class EventService {

	private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final int PAGE_SIZE = 10;

	private final EventRepository eventRepository;

	public EventService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@Transactional(readOnly = true)
	public PagedEventResponse getActiveEvents(int page, String keyword, String city, String category, String fromDateStr) {
		String kw  = isBlank(keyword)  ? null : keyword.trim();
		String ct  = isBlank(city)     ? null : city.trim();
		String cat = isBlank(category) ? null : category.trim();
		LocalDateTime fromDate = isBlank(fromDateStr) ? null : LocalDate.parse(fromDateStr).atStartOfDay();

		Specification<com.example.backend.model.EventEntity> spec = (root, query, cb) -> {
			Join<Object, Object> venue = root.join("venue", JoinType.INNER);
			Join<Object, Object> categoryJoin = root.join("category", JoinType.INNER);

			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("status"), EventStatus.ACTIVE));

			if (kw != null) {
				String pattern = "%" + kw.toLowerCase() + "%";
				predicates.add(cb.or(
					cb.like(cb.lower(root.get("title")), pattern),
					cb.like(cb.lower(venue.get("venueName")), pattern)
				));
			}
			if (ct != null) {
				predicates.add(cb.like(cb.lower(venue.get("city")), "%" + ct.toLowerCase() + "%"));
			}
			if (cat != null) {
				predicates.add(cb.equal(cb.lower(categoryJoin.get("categoryName")), cat.toLowerCase()));
			}
			if (fromDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), fromDate));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};

		Page<EventResponse> result = eventRepository
			.findAll(spec, PageRequest.of(page, PAGE_SIZE, Sort.by("eventDate").ascending()))
			.map(e -> new EventResponse(
				e.getEventId(),
				e.getTitle(),
				e.getDescription(),
				e.getEventDate().format(ISO_FORMATTER),
				e.getAvailableTickets(),
				e.getPrice(),
				e.getStatus().name(),
				e.getVenue().getVenueName(),
				e.getVenue().getCity(),
				e.getVenue().getAddress(),
				e.getVenue().getCapacity(),
				e.getCategory().getCategoryName()
			));
		return new PagedEventResponse(result.getContent(), !result.isLast());
	}

	private static boolean isBlank(String s) {
		return s == null || s.isBlank();
	}
}
