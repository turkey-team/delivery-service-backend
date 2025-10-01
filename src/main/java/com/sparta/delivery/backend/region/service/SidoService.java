package com.sparta.delivery.backend.region.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.region.dto.ReqCreateSidoDto;
import com.sparta.delivery.backend.region.dto.ResCreateSidoDto;
import com.sparta.delivery.backend.region.dto.ResReadSidoDto;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.repository.SidoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SidoService {

	private final SidoRepository sidoRepository;

	// 시·도 생성
	@Transactional
	public ResCreateSidoDto createSido(ReqCreateSidoDto requestDto) {
		if (sidoRepository.existsByName(requestDto.getName())) {
			log.warn("시/도 지역 이름 중복");
			throw new IllegalArgumentException("이미 존재하는 시/도 이름입니다.");
		}

		if (sidoRepository.existsByCode(requestDto.getCode())) {
			log.warn("시/도 지역 코드 중복");
			throw new IllegalArgumentException("이미 존재하는 시/도 코드입니다.");
		}

		Sido sido = Sido.builder()
			.name(requestDto.getName())
			.code(requestDto.getCode())
			.build();

		Sido savedSido = sidoRepository.save(sido);

		return ResCreateSidoDto.from(savedSido);
	}

	// 시·도 목록 조회
	public List<ResReadSidoDto> getAllSido() {
		return sidoRepository.findAll().stream()
			.map(ResReadSidoDto::from)
			.toList();
	}

}
