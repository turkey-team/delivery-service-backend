package com.sparta.delivery.backend.store.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class StoreController {

	private final StoreService storeService;

	/**
	 * 가게 등록
	 * @param requestDto
	 * @param @AuthenticationPrincipal UserDetailsImpl userDetails
	 * @return responseDto(Id, name)
	 * 추후 @AuthenticationPrincipal UserDetailsImpl userDetails로 변경
	 */
	@PostMapping("/stores")
	public ResCreateStoreDto createStore(@RequestBody @Valid ReqCreateStoreDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return storeService.createStore(requestDto, userDetails.getUser());
	}

	/**
	 *
	 * @param storeId
		상세조회
	 * @return resGetStoreDto
	 */
	@GetMapping("/stores/{storeId}")
	public ResGetStoreDto getStore(@PathVariable UUID storeId){
		return storeService.getStore(storeId);
	}


	/**
	 * 목록 조회
	 */
	@GetMapping("/stores")
	public Page<ResGetListStoreDto> getStores(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
		@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
		@RequestParam(value = "category", defaultValue = "") String categoryId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	){

		return storeService.getStores(page, size, sort, keyword, categoryId, userDetails.getUser());
	}

	/**
	 * 정보수정
	 * Owner만 가능
	 */
	@PatchMapping("/stores/{storeId}")
	public ResUpdateStoreInfoDto updateStoreInfo(@PathVariable UUID storeId, @RequestBody @Valid ReqUpdateStoreInfoDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return storeService.updateStoreInfo(storeId, requestDto, userDetails.getUser());
	}

	/**
	 * 배달 관련 수정
	 * Owner / Manager
	 * @param storeId
	 * @param requestDto
	 * @param userDetails
	 * @return
	 */
	@PatchMapping("/stores/{storeId}/details")
	public ResUpdateStoreDetailsDto updateStoreDetails(@PathVariable UUID storeId, @RequestBody @Valid ReqUpdateStoreDetailsDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return storeService.updateStoreDetails(storeId, requestDto, userDetails.getUser());
	}

	/**
	 * 가게 상태 수정
	 * Owner / Manager
	 * @param storeId
	 * @param requestDto
	 * @param userDetails
	 * @return
	 */
	@PatchMapping("/stores/{storeId}/status")
	public ResUpdateStoreStatusDto updateStoreStatus(@PathVariable UUID storeId, @RequestBody @Valid ReqUpdateStoreStatusDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return storeService.updateStoreStatus(storeId, requestDto, userDetails.getUser());
	}

	/**
	 * 가게 삭제
	 * @param storeId
	 * @param requestDto(businessNumber)
	 * @param userDetails
	 * @return
	 */
	@DeleteMapping("/stores/{storeId}")
	public ResDeleteStoreDto deleteStore(@PathVariable UUID storeId, @RequestBody @Valid ReqDeleteStoreDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return storeService.deleteStore(storeId, requestDto, userDetails.getUser());
	}

}
