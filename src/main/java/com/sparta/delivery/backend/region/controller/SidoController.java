package com.sparta.delivery.backend.region.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.region.dto.ReqCreateSidoDto;
import com.sparta.delivery.backend.region.dto.ResCreateSidoDto;
import com.sparta.delivery.backend.region.service.SidoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/regions")
@RequiredArgsConstructor
public class SidoController {

	private final SidoService sidoService;

	@PostMapping("/sido")
	public ResponseEntity<ResCreateSidoDto> save(@RequestBody ReqCreateSidoDto requestDto) {
		ResCreateSidoDto responseDto = sidoService.createSido(requestDto);

		return ResponseEntity.ok(responseDto);
	}

}
