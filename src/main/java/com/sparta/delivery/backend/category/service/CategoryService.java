package com.sparta.delivery.backend.category.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.category.dto.ResCreateCategoryDto;
import com.sparta.delivery.backend.category.dto.ResDeleteCategoryDto;
import com.sparta.delivery.backend.category.dto.ResGetCategoryDto;
import com.sparta.delivery.backend.category.dto.ResGetListCategoryDto;
import com.sparta.delivery.backend.category.dto.ResUpdateCategoryDto;
import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.category.repository.CategoryRepository;
import com.sparta.delivery.backend.user.entity.User;


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

	@Transactional(readOnly = true)
	public Page<ResGetListCategoryDto> getCategories(User user, String keyword, int page, int size, String sort) {

		if (size != 10 && size != 30 && size != 50){
			size = 10;
		}

		page = Math.max(page - 1, 0);

		Pageable pageable = createPageRequest(page, size, sort);

		Page<Category> categoryList;

		if (!keyword.isBlank()) {
			categoryList = categoryRepository.findAllByNameContainingAndDeletedAtIsNull(keyword, pageable);
		}else{
			categoryList = categoryRepository.findAllByDeletedAtIsNull(pageable);
		}

		return  categoryList.map(ResGetListCategoryDto::new);
	}


	public ResGetCategoryDto getCategory(UUID categoryId) {

		if (!categoryRepository.existsById(categoryId)) {
			throw new IllegalArgumentException("해당 카테고리는 존재하지 않습니다");
		}

		Category category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId).orElseThrow(()->new IllegalArgumentException("이미 삭제된 카테고리입니다."));

		ResGetCategoryDto resGetCategoryDto = ResGetCategoryDto.builder().id(category.getId()).categoryName(category.getName())
												.createdAt(category.getCreatedAt()).updatedAt(category.getUpdatedAt()).build();

		return resGetCategoryDto;
	}

	@Transactional
	public ResDeleteCategoryDto deleteCategory(UUID categoryId, User user) {

		Category category = categoryRepository.findById(categoryId).orElseThrow(()->new IllegalArgumentException("해당 카테고리가 존재하지 않습니다"));

		category.softDelete(user.getId());
		categoryRepository.save(category);

		ResDeleteCategoryDto deleteCategoryDto = ResDeleteCategoryDto.builder().categoryId(category.getId()).categoryName(category.getName()).build();

		return deleteCategoryDto;
	}

	private Pageable createPageRequest(int page, int size, String sortBy) {
		Sort sort;

		switch (sortBy){
			case "name":
				sort = Sort.by(Sort.Direction.ASC, "name");
				break;
			case "createdAtDesc":
				sort = Sort.by(Sort.Direction.DESC, "createdAt");
				break;
			case "createdAtAsc":
				sort = Sort.by(Sort.Direction.ASC, "createdAt");
				break;
			default:
				sort = Sort.by(Sort.Direction.ASC, "createdAt");
				break;
		}

		return PageRequest.of(page, size, sort);
	}

	public boolean checkExistCategory(String categoryName){
		return categoryRepository.existsByName(categoryName);
	}

}
