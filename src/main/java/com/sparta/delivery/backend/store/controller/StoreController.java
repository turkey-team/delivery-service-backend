package com.sparta.delivery.backend.store.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.store.dto.ReqCreateStoreDto;
import com.sparta.delivery.backend.store.dto.ResCreateStoreDto;
import com.sparta.delivery.backend.store.dto.ResGetListStoreDto;
import com.sparta.delivery.backend.store.dto.ResGetStoreDto;
import com.sparta.delivery.backend.store.service.StoreService;
import com.sparta.delivery.backend.user.entity.User;

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
	// @GetMapping("/stores")
	// public Page<ResGetListStoreDto> getStores(
	// 	@RequestParam(value = "page", required = false, defaultValue = "0") int page,
	// 	@RequestParam(value = "size", required = false, defaultValue = "10") int size,
	// 	@RequestParam("sortBy") String sortBy,
	// 	@RequestParam("isAsc") boolean isAsc
	// ){
	// 	return null;
	// }

	/**
	 * 수정
	 */
}
