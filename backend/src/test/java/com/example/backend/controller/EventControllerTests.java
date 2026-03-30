package com.example.backend.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.backend.service.EventService;

@WebMvcTest(EventController.class)
class EventControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private EventService eventService;

	@Test
	void getActiveEventsReturns200WithPagedResponse() throws Exception {
		List<EventResponse> events = List.of(
			new EventResponse(1, "Jazz Night", "Great show", "2026-06-01T20:00:00", 100,
				new BigDecimal("49.99"), "ACTIVE", "Bell Centre", "Montreal", "1909 Av.", 21302, "Concert")
		);
		when(eventService.getActiveEvents(0, null, null, null, null))
			.thenReturn(new PagedEventResponse(events, false));

		mockMvc.perform(get("/api/events"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.events[0].title").value("Jazz Night"))
			.andExpect(jsonPath("$.events[0].venueCity").value("Montreal"))
			.andExpect(jsonPath("$.hasMore").value(false));
	}

	@Test
	void getActiveEventsPassesAllQueryParams() throws Exception {
		when(eventService.getActiveEvents(2, "jazz", "Montreal", "Concert", "2026-07-01"))
			.thenReturn(new PagedEventResponse(List.of(), false));

		mockMvc.perform(get("/api/events")
				.param("page", "2")
				.param("keyword", "jazz")
				.param("city", "Montreal")
				.param("category", "Concert")
				.param("fromDate", "2026-07-01"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.events").isEmpty());

		verify(eventService).getActiveEvents(2, "jazz", "Montreal", "Concert", "2026-07-01");
	}

	@Test
	void getActiveEventsDefaultsPageToZero() throws Exception {
		when(eventService.getActiveEvents(0, null, null, null, null))
			.thenReturn(new PagedEventResponse(List.of(), false));

		mockMvc.perform(get("/api/events"))
			.andExpect(status().isOk());

		verify(eventService).getActiveEvents(0, null, null, null, null);
	}
}
