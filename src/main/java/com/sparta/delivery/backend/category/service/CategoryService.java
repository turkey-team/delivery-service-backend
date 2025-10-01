package com.sparta.delivery.backend.category.service;

import org.springframework.stereotype.Service;

import com.sparta.delivery.backend.category.dto.CategoryResponseDto;
import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;


}
