package com.sparta.delivery.backend.category.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.category.dto.ReqCreateCategoryDto;
import com.sparta.delivery.backend.category.dto.ReqUpdateCategoryDto;
import com.sparta.delivery.backend.category.dto.ResCreateCategoryDto;
import com.sparta.delivery.backend.category.dto.ResDeleteCategoryDto;
import com.sparta.delivery.backend.category.dto.ResGetCategoryDto;
import com.sparta.delivery.backend.category.dto.ResGetListCategoryDto;
import com.sparta.delivery.backend.category.dto.ResUpdateCategoryDto;
import com.sparta.delivery.backend.category.service.CategoryService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CategoryController {

	private final CategoryService categoryService;

	/**
	 * 카테고리 생성
	 * @param reqCreateCategoryDto 카테고리 이름
	 * @param userDetails 로그인 유저
	 * @return 생성한 카테고리
	 */
	@PostMapping("/categories")
	public ResCreateCategoryDto createCategory(@RequestBody ReqCreateCategoryDto reqCreateCategoryDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
	 	return categoryService.createCategory(reqCreateCategoryDto.getName(), userDetails.getUser());
	}

	/**
	 * 카테고리 단건조회
	 * @param categoryId
	 * @return
	 */
	@GetMapping("/categories/{categoryId}")
	public ResGetCategoryDto getCategory(@PathVariable UUID categoryId){
		return categoryService.getCategory(categoryId);
	}

	/**
	 * 카테고리 페이징, 키워드 검색
	 * @param userDetails
	 * @param keyword
	 * @param size
	 * @param sort
	 * @param page
	 * @return
	 */
	@GetMapping("/categories")
	public Page<ResGetListCategoryDto> getCategories(@AuthenticationPrincipal UserDetailsImpl userDetails,
														@RequestParam(required = false, defaultValue = "") String keyword,
														@RequestParam(defaultValue = "10") int size,
														@RequestParam(defaultValue = "createdAtAsc") String sort,
														@RequestParam(defaultValue = "1") int page
		){
		return categoryService.getCategories(userDetails.getUser(), keyword, page, size, sort);
	}

	/**
	 * 카테고리 수정
	 * @param categoryId
	 * @param reqUpdateCategoryDto
	 * @param userDetails
	 * @return
	 */
	@PutMapping("/categories/{categoryId}")
	public ResUpdateCategoryDto updateCategory(@PathVariable UUID categoryId, @RequestBody ReqUpdateCategoryDto reqUpdateCategoryDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return categoryService.updateCategory(categoryId, reqUpdateCategoryDto.getName(), userDetails.getUser());
	}

	/**
	 * 카테고리 삭제
	 * @param categoryId
	 * @param userDetails
	 * @return
	 */
	@DeleteMapping("/categories/{categoryId}")
	public ResDeleteCategoryDto deleteCategory(@PathVariable UUID categoryId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return categoryService.deleteCategory(categoryId, userDetails.getUser());
	}


}
