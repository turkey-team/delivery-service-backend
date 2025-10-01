package com.sparta.delivery.backend.region.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.region.dto.ReqCreateSigunguDto;
import com.sparta.delivery.backend.region.dto.ReqUpdateSigunguDto;
import com.sparta.delivery.backend.region.dto.ResCreateSigunguDto;
import com.sparta.delivery.backend.region.dto.ResReadSigunguDto;
import com.sparta.delivery.backend.region.dto.ResUpdateSigunguDto;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.region.repository.SidoRepository;
import com.sparta.delivery.backend.region.repository.SigunguRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SigunguService {

	private final SigunguRepository sigunguRepository;
	private final SidoRepository sidoRepository;

	// 시·군·구 생성
	@Transactional
	public ResCreateSigunguDto createSigungu(UUID sidoId, ReqCreateSigunguDto requestDto) {
		Sido sido = sidoRepository.findById(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/도입니다.");
		});

		if (sigunguRepository.existsByName(requestDto.getName())) {
			log.warn("시/군/구 지역 이름 중복");
			throw new IllegalArgumentException("이미 존재하는 시/군/구 이름입니다.");
		}

		if (sigunguRepository.existsByCode(requestDto.getCode())) {
			log.warn("시/군/구 지역 코드 중복");
			throw new IllegalArgumentException("이미 존재하는 시/군/구 코드입니다.");
		}

		Sigungu sigungu = Sigungu.builder()
			.sido(sido)
			.name(requestDto.getName())
			.code(requestDto.getCode())
			.build();

		Sigungu savedSigungu = sigunguRepository.save(sigungu);

		return ResCreateSigunguDto.from(savedSigungu);
	}

	// 시·군·구 목록 조회
	public List<ResReadSigunguDto> getAllSigungu(UUID sidoId) {
		Sido sido = sidoRepository.findById(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/도입니다.");
		});

		return sigunguRepository.findAllBySido(sido).stream()
			.map(ResReadSigunguDto::from)
			.toList();
	}

	// 시·군·구 수정
	@Transactional
	public ResUpdateSigunguDto updateSigungu(UUID sidoId, UUID sigunguId, ReqUpdateSigunguDto requestDto) {
		Sido sido = sidoRepository.findById(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/도입니다.");
		});

		Sigungu sigungu = sigunguRepository.findByIdAndSido(sigunguId, sido).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		if (sigunguRepository.existsByNameAndIdNot(requestDto.getName(), sigungu.getId())) {
			log.warn("시/군/구 지역 이름 중복");
			throw new IllegalArgumentException("이미 존재하는 시/군/구 이름입니다.");
		}

		if (sigunguRepository.existsByCodeAndIdNot(requestDto.getCode(), sigungu.getId())) {
			log.warn("시/군/구 지역 코드 중복");
			throw new IllegalArgumentException("이미 존재하는 시/군/구 코드입니다.");
		}

		sigungu.update(sido, requestDto.getName(), requestDto.getCode());

		return ResUpdateSigunguDto.from(sigungu);
	}

	@Transactional
	public void deleteSigungu(UUID sidoId, UUID sigunguId) {
		Sido sido = sidoRepository.findById(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/도입니다.");
		});

		Sigungu sigungu = sigunguRepository.findByIdAndSido(sigunguId, sido).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		// 임시로 null을 넘김
		sigungu.softDelete(null);
	}

}
