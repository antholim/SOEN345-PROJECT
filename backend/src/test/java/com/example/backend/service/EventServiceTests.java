package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.example.backend.controller.PagedEventResponse;
import com.example.backend.model.CategoryEntity;
import com.example.backend.model.EventEntity;
import com.example.backend.model.EventRepository;
import com.example.backend.model.EventStatus;
import com.example.backend.model.VenueEntity;

@ExtendWith(MockitoExtension.class)
class EventServiceTests {

	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private EventService eventService;

	@Captor
	private ArgumentCaptor<Pageable> pageableCaptor;

	private EventEntity makeEvent(int id, String title) {
		return makeEvent(id, title, "Bell Centre", "Montreal", "Concert");
	}

	private EventEntity makeEvent(int id, String title, String venueName, String city, String categoryName) {
		VenueEntity venue = new VenueEntity();
		venue.setVenueName(venueName);
		venue.setCity(city);

		CategoryEntity category = new CategoryEntity();
		category.setCategoryName(categoryName);

		EventEntity event = new EventEntity();
		event.setEventId(id);
		event.setTitle(title);
		event.setDescription("A great show");
		event.setEventDate(LocalDateTime.of(2026, 6, 1, 20, 0));
		event.setAvailableTickets(100);
		event.setPrice(new BigDecimal("99.99"));
		event.setStatus(EventStatus.ACTIVE);
		event.setVenue(venue);
		event.setCategory(category);
		return event;
	}

	@SuppressWarnings("unchecked")
	private void givenRepoReturns(Page<EventEntity> page) {
		when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
	}

	// ── Pagination ────────────────────────────────────────────────────────────

	@Test
	void getActiveEventsReturnsEventsOnFirstPage() {
		givenRepoReturns(new PageImpl<>(List.of(makeEvent(1, "Show A"), makeEvent(2, "Show B"))));

		PagedEventResponse response = eventService.getActiveEvents(0, null, null, null, null);

		assertEquals(2, response.events().size());
		assertEquals("Show A", response.events().get(0).title());
		assertEquals("Show B", response.events().get(1).title());
	}

	@Test
	void getActiveEventsMapsFieldsCorrectly() {
		givenRepoReturns(new PageImpl<>(List.of(makeEvent(5, "Jazz Night"))));

		PagedEventResponse response = eventService.getActiveEvents(0, null, null, null, null);

		var event = response.events().get(0);
		assertEquals(5, event.eventId());
		assertEquals("Jazz Night", event.title());
		assertEquals("Bell Centre", event.venueName());
		assertEquals("Montreal", event.venueCity());
		assertEquals("Concert", event.categoryName());
		assertEquals(new BigDecimal("99.99"), event.price());
		assertEquals("ACTIVE", event.status());
	}

	@Test
	void getActiveEventsHasMoreTrueWhenNotLastPage() {
		givenRepoReturns(new PageImpl<>(List.of(makeEvent(1, "Show A")), PageRequest.of(0, 10), 20));

		PagedEventResponse response = eventService.getActiveEvents(0, null, null, null, null);

		assertTrue(response.hasMore());
	}

	@Test
	void getActiveEventsHasMoreFalseOnLastPage() {
		givenRepoReturns(new PageImpl<>(List.of(makeEvent(1, "Show A")), PageRequest.of(0, 10), 1));

		PagedEventResponse response = eventService.getActiveEvents(0, null, null, null, null);

		assertFalse(response.hasMore());
	}

	@Test
	void getActiveEventsReturnsEmptyListWhenNoEvents() {
		givenRepoReturns(Page.empty());

		PagedEventResponse response = eventService.getActiveEvents(0, null, null, null, null);

		assertTrue(response.events().isEmpty());
		assertFalse(response.hasMore());
	}

	@SuppressWarnings("unchecked")
	@Test
	void getActiveEventsUsesEventDateAscendingSort() {
		givenRepoReturns(Page.empty());

		eventService.getActiveEvents(0, null, null, null, null);

		verify(eventRepository).findAll(any(Specification.class), pageableCaptor.capture());
		Sort.Order order = pageableCaptor.getValue().getSort().getOrderFor("eventDate");
		assertTrue(order != null && order.isAscending());
	}

	// ── Filter: blank values treated as no filter ─────────────────────────────

	@Test
	void blankCityIsIgnored() {
		givenRepoReturns(new PageImpl<>(List.of(makeEvent(1, "Show A"))));

		PagedEventResponse response = eventService.getActiveEvents(0, null, "   ", null, null);

		assertEquals(1, response.events().size());
	}

	@Test
	void blankCategoryIsIgnored() {
		givenRepoReturns(new PageImpl<>(List.of(makeEvent(1, "Show A"))));

		PagedEventResponse response = eventService.getActiveEvents(0, null, null, "", null);

		assertEquals(1, response.events().size());
	}

	@Test
	void blankFromDateIsIgnored() {
		givenRepoReturns(new PageImpl<>(List.of(makeEvent(1, "Show A"))));

		PagedEventResponse response = eventService.getActiveEvents(0, null, null, null, "  ");

		assertEquals(1, response.events().size());
	}

	// ── Filter: results returned correctly ────────────────────────────────────

	@Test
	void filterByCityReturnsMatchingEvents() {
		List<EventEntity> torontoEvents = List.of(
			makeEvent(10, "Blue Jays Game", "Rogers Centre", "Toronto", "Sports")
		);
		givenRepoReturns(new PageImpl<>(torontoEvents));

		PagedEventResponse response = eventService.getActiveEvents(0, null, "Toronto", null, null);

		assertEquals(1, response.events().size());
		assertEquals("Blue Jays Game", response.events().get(0).title());
		assertEquals("Toronto", response.events().get(0).venueCity());
	}

	@Test
	void filterByCategoryReturnsMatchingEvents() {
		List<EventEntity> comedyEvents = List.of(
			makeEvent(20, "Kevin Hart Live", "MTelus", "Montreal", "Comedy")
		);
		givenRepoReturns(new PageImpl<>(comedyEvents));

		PagedEventResponse response = eventService.getActiveEvents(0, null, null, "Comedy", null);

		assertEquals(1, response.events().size());
		assertEquals("Comedy", response.events().get(0).categoryName());
	}

	@Test
	void filterByFromDateReturnsMatchingEvents() {
		givenRepoReturns(new PageImpl<>(List.of(makeEvent(1, "Future Show"))));

		PagedEventResponse response = eventService.getActiveEvents(0, null, null, null, "2026-07-01");

		assertEquals(1, response.events().size());
	}

	@Test
	void filterByCityAndCategoryReturnsCombinedResults() {
		List<EventEntity> events = List.of(
			makeEvent(30, "Coldplay", "Bell Centre", "Montreal", "Concert")
		);
		givenRepoReturns(new PageImpl<>(events));

		PagedEventResponse response = eventService.getActiveEvents(0, null, "Montreal", "Concert", null);

		assertEquals(1, response.events().size());
		assertEquals("Coldplay", response.events().get(0).title());
	}

	@Test
	void filterWithNoMatchesReturnsEmptyList() {
		givenRepoReturns(Page.empty());

		PagedEventResponse response = eventService.getActiveEvents(0, null, "Atlantis", "Opera", null);

		assertTrue(response.events().isEmpty());
		assertFalse(response.hasMore());
	}
}
