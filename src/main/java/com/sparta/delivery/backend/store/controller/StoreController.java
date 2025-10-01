package com.sparta.delivery.backend.store.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.store.dto.StoreRequestDto;
import com.sparta.delivery.backend.store.dto.StoreResponseDto;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class StoreController {

	private final StoreService storeService;

	@PostMapping("/stores")
	public StoreResponseDto createStore(@RequestBody StoreRequestDto requestDto
		//,@AuthenticationPrincipal UserDetailsImpl userDetails
		){
		return storeService.createStore(requestDto/*, userDetails.getUser()*/);
	}
}
