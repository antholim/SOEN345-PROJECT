package com.example.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.CategoryRepository;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryRepository categoryRepository;

	public CategoryController(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@GetMapping
	public List<String> getCategories() {
		return categoryRepository.findAllCategoryNames();
	}
}
