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

import com.sparta.delivery.backend.cart.dto.ResCreateCartDto;
import com.sparta.delivery.backend.category.dto.ReqCreateCategoryDto;
import com.sparta.delivery.backend.category.dto.ReqUpdateCategoryDto;
import com.sparta.delivery.backend.category.dto.ResCreateCategoryDto;
import com.sparta.delivery.backend.category.dto.ResDeleteCategoryDto;
import com.sparta.delivery.backend.category.dto.ResGetCategoryDto;
import com.sparta.delivery.backend.category.dto.ResGetListCategoryDto;
import com.sparta.delivery.backend.category.dto.ResUpdateCategoryDto;
import com.sparta.delivery.backend.category.service.CategoryService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Tag(name="Category-Controller", description = "카테고리 관련 API")
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping("/categories")
	@Operation(summary = "카테고리 추가", description = "카테고리를 추가합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "카테고리 추가 성공"
			,content = @Content(schema = @Schema(implementation = ResCreateCategoryDto.class)))
		,@ApiResponse(responseCode = "400", description = "카테고리명 중복")
		,@ApiResponse(responseCode = "403", description = "매니저 권한이 아니면 생성 불가")
	})
	public ResCreateCategoryDto createCategory(@RequestBody ReqCreateCategoryDto reqCreateCategoryDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
	 	return categoryService.createCategory(reqCreateCategoryDto.getName(), userDetails.getUser());
	}

	@GetMapping("/categories/{categoryId}")
	@Operation(summary = "카테고리 조회", description = "카테고리 상세 조회")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "카테고리 조회 성공"
			,content = @Content(schema = @Schema(implementation = ResGetCategoryDto.class)))
		,@ApiResponse(responseCode = "400", description = "카테고리 없음")
	})
	public ResGetCategoryDto getCategory(@PathVariable UUID categoryId){
		return categoryService.getCategory(categoryId);
	}

	@GetMapping("/categories")
	@Operation(summary = "카테고리 목록 조회", description = "카테고리 목록 조회")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공"
			,content = @Content(schema = @Schema(implementation = ResGetListCategoryDto.class)))
	})
	public Page<ResGetListCategoryDto> getCategories(@AuthenticationPrincipal UserDetailsImpl userDetails,
														@RequestParam(required = false, defaultValue = "") String keyword,
														@RequestParam(defaultValue = "10") int size,
														@RequestParam(defaultValue = "createdAtAsc") String sort,
														@RequestParam(defaultValue = "1") int page
		){
		return categoryService.getCategories(userDetails.getUser(), keyword, page, size, sort);
	}

	@PutMapping("/categories/{categoryId}")
	@Operation(summary = "카테고리 수정", description = "카테고리 이름 수정")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "카테고리 수정 성공"
			,content = @Content(schema = @Schema(implementation = ResGetCategoryDto.class)))
		,@ApiResponse(responseCode = "400", description = "카테고리 없음 혹은 카테고리명 중복")
	})
	public ResUpdateCategoryDto updateCategory(@PathVariable UUID categoryId, @RequestBody ReqUpdateCategoryDto reqUpdateCategoryDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return categoryService.updateCategory(categoryId, reqUpdateCategoryDto.getName(), userDetails.getUser());
	}

	@DeleteMapping("/categories/{categoryId}")
	@Operation(summary = "카테고리 삭제", description = "카테고리 삭제")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "카테고리 삭제 성공"
			,content = @Content(schema = @Schema(implementation = ResDeleteCategoryDto.class)))
		,@ApiResponse(responseCode = "400", description = "카테고리 없음 혹은 사용중인 카테고리")
		,@ApiResponse(responseCode = "403", description = "매니저 권한이 아니면 생성 불가")
	})
	public ResDeleteCategoryDto deleteCategory(@PathVariable UUID categoryId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return categoryService.deleteCategory(categoryId, userDetails.getUser());
	}


}
