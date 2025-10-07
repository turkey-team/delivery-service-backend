package com.sparta.delivery.backend.category.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
	boolean existsByName(String name);

	Optional<Category> findByIdAndDeletedAtIsNull(UUID categoryId);

	Page<Category> findAllByDeletedAtIsNull(Pageable pageable);

	Page<Category> findAllByNameContainingAndDeletedAtIsNull(String keyword, Pageable pageable);
}
