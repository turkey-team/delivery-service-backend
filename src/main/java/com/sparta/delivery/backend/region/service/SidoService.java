package com.sparta.delivery.backend.region.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.region.dto.ReqCreateSidoDto;
import com.sparta.delivery.backend.region.dto.ReqUpdateDto;
import com.sparta.delivery.backend.region.dto.ResCreateSidoDto;
import com.sparta.delivery.backend.region.dto.ResReadSidoDto;
import com.sparta.delivery.backend.region.dto.ResUpdateDto;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.repository.SidoRepository;

import jakarta.persistence.EntityNotFoundException;
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

	// 시·도 수정
	@Transactional
	public ResUpdateDto updateSido(UUID sidoId, ReqUpdateDto requestDto) {
		Sido sido = sidoRepository.findById(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/도입니다.");
		});

		if (sidoRepository.existsByNameAndIdNot(requestDto.getName(), sidoId)) {
			log.warn("시/도 지역 이름 중복");
			throw new IllegalArgumentException("이미 존재하는 시/도 이름입니다.");
		}

		if (sidoRepository.existsByCodeAndIdNot(requestDto.getCode(), sidoId)) {
			log.warn("시/도 지역 코드 중복");
			throw new IllegalArgumentException("이미 존재하는 시/도 코드입니다.");
		}

		sido.update(requestDto.getName(), requestDto.getCode());

		return ResUpdateDto.from(sido);
	}

	// 시·도 삭제
	public void deleteSido(UUID sidoId) {
		Sido sido = sidoRepository.findById(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/도입니다.");
		});

		// 임시로 null을 넘김
		sido.softDelete(null);
	}

}
