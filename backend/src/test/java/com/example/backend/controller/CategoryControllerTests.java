package com.example.backend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.backend.model.CategoryRepository;

@WebMvcTest(CategoryController.class)
class CategoryControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CategoryRepository categoryRepository;

	@Test
	void getCategoriesReturns200WithStringList() throws Exception {
		when(categoryRepository.findAllCategoryNames())
			.thenReturn(List.of("Comedy", "Concert", "Sports"));

		mockMvc.perform(get("/api/categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0]").value("Comedy"))
			.andExpect(jsonPath("$[1]").value("Concert"))
			.andExpect(jsonPath("$[2]").value("Sports"))
			.andExpect(jsonPath("$.length()").value(3));
	}

	@Test
	void getCategoriesReturnsEmptyArrayWhenNone() throws Exception {
		when(categoryRepository.findAllCategoryNames()).thenReturn(List.of());

		mockMvc.perform(get("/api/categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isEmpty());
	}
}
