package com.sparta.delivery.backend.category.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.category.dto.ReqCreateCategoryDto;
import com.sparta.delivery.backend.category.dto.ReqUpdateCategoryDto;
import com.sparta.delivery.backend.category.dto.ResCreateCategoryDto;
import com.sparta.delivery.backend.category.dto.ResUpdateCategoryDto;
import com.sparta.delivery.backend.category.service.CategoryService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CategoryController {

	private final CategoryService categoryService;

	/**
	 *
	 * @param reqCreateCategoryDto 카테고리 이름
	 * @param userDetails 로그인 유저
	 * @return 생성한 카테고리
	 */
	@PostMapping("/category")
	public ResCreateCategoryDto createCategory(@RequestBody ReqCreateCategoryDto reqCreateCategoryDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
	 	return categoryService.createCategory(reqCreateCategoryDto.getName(), userDetails.getUser());
	}

	/**
	 *
	 * @param categoryId
	 * @param reqUpdateCategoryDto
	 * @param userDetails
	 * @return
	 */
	@PutMapping("/category/{categoryId}")
	public ResUpdateCategoryDto updateCategory(@PathVariable UUID categoryId, @RequestBody ReqUpdateCategoryDto reqUpdateCategoryDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return categoryService.updateCategory(categoryId, reqUpdateCategoryDto.getName(), userDetails.getUser());
	}

}
