package com.sparta.delivery.backend.store.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.global.common.dto.PageResponse;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.store.dto.ReqCreateStoreDto;
import com.sparta.delivery.backend.store.dto.ReqDeleteStoreDto;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreDetailsDto;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreInfoDto;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreStatusDto;
import com.sparta.delivery.backend.store.dto.ResCreateStoreDto;
import com.sparta.delivery.backend.store.dto.ResDeleteStoreDto;
import com.sparta.delivery.backend.store.dto.ResGetListStoreDto;
import com.sparta.delivery.backend.store.dto.ResGetStoreDto;
import com.sparta.delivery.backend.store.dto.ResUpdateStoreDetailsDto;
import com.sparta.delivery.backend.store.dto.ResUpdateStoreInfoDto;
import com.sparta.delivery.backend.store.dto.ResUpdateStoreStatusDto;
import com.sparta.delivery.backend.store.service.StoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Tag(name="Store-Controller", description = "가게 관련 API")
public class StoreController {

	private final StoreService storeService;

	@PostMapping("/stores")
	@Operation(summary = "가게 추가", description = "가게를 추가합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "가게 추가 성공"
			,content = @Content(schema = @Schema(implementation = ResCreateStoreDto.class)))
		,@ApiResponse(responseCode = "400", description = "주소지 오류")
		,@ApiResponse(responseCode = "400", description = "이미지 URL 중복")
		,@ApiResponse(responseCode = "403", description = "Manager 혹은 Owner 아니면 생성 불가")
	})
	@PreAuthorize("hasAnyRole('MASTER', 'MANAGER', 'OWNER')")
	public ResCreateStoreDto createStore(@RequestBody @Valid ReqCreateStoreDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return storeService.createStore(requestDto, userDetails.getUser());
	}

	@GetMapping("/stores/{storeId}")
	@Operation(summary = "가게 상세 조회", description = "가게를 조회합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "가게 조회 성공"
			,content = @Content(schema = @Schema(implementation = ResGetStoreDto.class)))
		,@ApiResponse(responseCode = "400", description = "가게 존재하지않음")
	})
	@PreAuthorize("isAuthenticated() && hasAnyRole('MANAGER', 'OWNER', 'CUSTOMER')")
	public ResGetStoreDto getStore(@PathVariable UUID storeId){
		return storeService.getStore(storeId);
	}


	@GetMapping("/stores")
	@Operation(summary = "가게 목록 조회", description = "가게 목록을 조회합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "가게 목록 조회 성공"
			,content = @Content(schema = @Schema(implementation = ResGetListStoreDto.class)))
		,@ApiResponse(responseCode = "400", description = "카테고리 없음")
	})
	@PreAuthorize("isAuthenticated() && hasAnyRole('MANAGER', 'OWNER', 'CUSTOMER')")
	public PageResponse<ResGetListStoreDto> getStores(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sort", required = false) String sort,
		@RequestParam(value = "category", required = false) UUID categoryId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	){
		return storeService.getStores(page, size, sort, categoryId, userDetails.getUser());
	}

	@PatchMapping("/stores/{storeId}")
	@Operation(summary = "가게 수정", description = "가게 정보를 수정합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "가게 정보 수정 성공"
			,content = @Content(schema = @Schema(implementation = ResUpdateStoreInfoDto.class)))
		,@ApiResponse(responseCode = "400", description = "가게 없음 혹은 주소지, 이미지 없음")
		,@ApiResponse(responseCode = "400", description = "이미지 URL 중복")
		,@ApiResponse(responseCode = "403", description = "Manager 혹은 Owner 아니면 수정 불가")
	})
	@PreAuthorize("hasAnyRole('MASTER', 'MANAGER', 'OWNER')")
	public ResUpdateStoreInfoDto updateStoreInfo(@PathVariable UUID storeId, @RequestBody @Valid ReqUpdateStoreInfoDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return storeService.updateStoreInfo(storeId, requestDto, userDetails.getUser());
	}

	@PatchMapping("/stores/{storeId}/details")
	@Operation(summary = "가게 배달 정보 수정", description = "가게의 배달 관련된 정보를 수정합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "가게 정보 수정 성공"
			,content = @Content(schema = @Schema(implementation = ResUpdateStoreDetailsDto.class)))
		,@ApiResponse(responseCode = "400", description = "가게 없음")
		,@ApiResponse(responseCode = "403", description = "Manager 혹은 Owner 아니면 수정 불가")
	})
	@PreAuthorize("hasAnyRole('MASTER', 'MANAGER', 'OWNER')")
	public ResUpdateStoreDetailsDto updateStoreDetails(@PathVariable UUID storeId, @RequestBody @Valid ReqUpdateStoreDetailsDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return storeService.updateStoreDetails(storeId, requestDto, userDetails.getUser());
	}

	@PatchMapping("/stores/{storeId}/status")
	@Operation(summary = "가게 상태 정보 수정", description = "가게 상태 정보를 수정합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "가게 상태 정보 수정 성공"
			,content = @Content(schema = @Schema(implementation = ResUpdateStoreStatusDto.class)))
		,@ApiResponse(responseCode = "400", description = "가게 없음 혹은 현재와 동일한 상태로 변경 요청")
		,@ApiResponse(responseCode = "403", description = "Manager 혹은 Owner 아니면 수정 불가")
	})
	@PreAuthorize("hasAnyRole('MASTER', 'MANAGER', 'OWNER')")
	public ResUpdateStoreStatusDto updateStoreStatus(@PathVariable UUID storeId, @RequestBody @Valid ReqUpdateStoreStatusDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return storeService.updateStoreStatus(storeId, requestDto, userDetails.getUser());
	}

	@DeleteMapping("/stores/{storeId}")
	@Operation(summary = "가게 삭제", description = "가게를 삭제합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "가게 삭제 수정 성공"
			,content = @Content(schema = @Schema(implementation = ResUpdateStoreInfoDto.class)))
		,@ApiResponse(responseCode = "400", description = "가게 없음")
		,@ApiResponse(responseCode = "403", description = "Manager 혹은 Owner 아니면 삭제 불가")
	})
	@PreAuthorize("hasAnyRole('MASTER', 'MANAGER', 'OWNER')")
	public ResDeleteStoreDto deleteStore(@PathVariable UUID storeId, @RequestBody @Valid ReqDeleteStoreDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return storeService.deleteStore(storeId, requestDto, userDetails.getUser());
	}

}
