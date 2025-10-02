package com.sparta.delivery.backend.category.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sparta.delivery.backend.category.dto.ResCreateCategoryDto;
import com.sparta.delivery.backend.category.dto.ResUpdateCategoryDto;
import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.category.repository.CategoryRepository;
import com.sparta.delivery.backend.user.entity.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	@Transactional
	public ResCreateCategoryDto createCategory(String name, User user) {

		//MASTER권한확인
		// if (!(user.getRole()== UserRoleEnum.MASTER)){
		// 	throw new IllegalArgumentException("권한이없습니다");
		// }

		boolean flag = checkExistCategory(name);

		//카테고리명 중복일때
		 if (flag) {
		 	throw new IllegalArgumentException(name+"은(는) 중복된 카테고리 이름입니다");
		 }

		Category category = Category.builder().name(name).build();
		categoryRepository.save(category);

		return new ResCreateCategoryDto(category);

	}

	@Transactional
	public ResUpdateCategoryDto updateCategory(UUID id, String name, User user) {

		Category category = categoryRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

		if (category.getName().equals(name)) {
			throw new IllegalArgumentException("수정하려는 이름과 기존 카테고리 명이 중복됩니다.");
		}

		category.updateCategoryName(name);

		categoryRepository.save(category);

		return new ResUpdateCategoryDto(category);

	}

	public boolean checkExistCategory(String categoryName){
		return categoryRepository.existsByName(categoryName);
	}
}
