package com.example.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.service.AdminEventService;

@WebMvcTest(AdminEventController.class)
class AdminEventControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminEventService adminEventService;

	private static final String EVENT_JSON = """
		{
			"title": "Rock Night",
			"description": "Amazing concert",
			"eventDate": "2026-06-15T20:00:00",
			"availableTickets": 200,
			"price": 49.99,
			"venueName": "Bell Centre",
			"venueCity": "Montreal",
			"venueAddress": "1909 Av.",
			"venueCapacity": 21302,
			"categoryName": "Concert",
			"createdBy": 1
		}
		""";

	private EventResponse sampleResponse() {
		return new EventResponse(10, "Rock Night", "Amazing concert", "2026-06-15T20:00:00",
			200, new BigDecimal("49.99"), "ACTIVE", "Bell Centre", "Montreal", "1909 Av.", 21302, "Concert");
	}

	@Test
	void createEventReturns201() throws Exception {
		when(adminEventService.createEvent(any(CreateEventRequest.class))).thenReturn(sampleResponse());

		mockMvc.perform(post("/api/admin/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(EVENT_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.eventId").value(10))
			.andExpect(jsonPath("$.title").value("Rock Night"))
			.andExpect(jsonPath("$.status").value("ACTIVE"));
	}

	@Test
	void updateEventReturns200() throws Exception {
		when(adminEventService.updateEvent(eq(10), any(CreateEventRequest.class))).thenReturn(sampleResponse());

		mockMvc.perform(put("/api/admin/events/10")
				.contentType(MediaType.APPLICATION_JSON)
				.content(EVENT_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.eventId").value(10))
			.andExpect(jsonPath("$.title").value("Rock Night"));
	}

	@Test
	void cancelEventReturns204() throws Exception {
		doNothing().when(adminEventService).cancelEvent(10);

		mockMvc.perform(patch("/api/admin/events/10/cancel"))
			.andExpect(status().isNoContent());
	}

	@Test
	void cancelEventPropagates404() throws Exception {
		doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"))
			.when(adminEventService).cancelEvent(99);

		mockMvc.perform(patch("/api/admin/events/99/cancel"))
			.andExpect(status().isNotFound());
	}

	@Test
	void cancelEventPropagates409() throws Exception {
		doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Already cancelled"))
			.when(adminEventService).cancelEvent(10);

		mockMvc.perform(patch("/api/admin/events/10/cancel"))
			.andExpect(status().isConflict());
	}

	@Test
	void createEventPropagates400WhenCategoryNotFound() throws Exception {
		when(adminEventService.createEvent(any(CreateEventRequest.class)))
			.thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));

		mockMvc.perform(post("/api/admin/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(EVENT_JSON))
			.andExpect(status().isBadRequest());
	}
}
