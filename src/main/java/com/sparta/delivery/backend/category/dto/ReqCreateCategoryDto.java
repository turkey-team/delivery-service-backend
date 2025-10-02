package com.sparta.delivery.backend.category.dto;

import com.sparta.delivery.backend.category.repository.CategoryRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqCreateCategoryDto {
	private String name;

	public String getName() {
		return name;
	}
}
