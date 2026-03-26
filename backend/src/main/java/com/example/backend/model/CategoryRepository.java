package com.example.backend.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

	Optional<CategoryEntity> findByCategoryName(String categoryName);

	@Query("SELECT c.categoryName FROM CategoryEntity c ORDER BY c.categoryName ASC")
	List<String> findAllCategoryNames();
}
