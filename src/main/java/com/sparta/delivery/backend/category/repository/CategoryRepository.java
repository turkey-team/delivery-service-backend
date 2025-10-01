package com.sparta.delivery.backend.category.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
