package com.sparta.delivery.backend.region.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.region.dto.ReqCreateDongDto;
import com.sparta.delivery.backend.region.dto.ResCreateDongDto;
import com.sparta.delivery.backend.region.dto.ResReadDongDto;
import com.sparta.delivery.backend.region.dto.ResUpdateDongDto;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.region.repository.SigunguRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DongService {

	private final DongRepository dongRepository;
	private final SigunguRepository sigunguRepository;

	// 동 생성
	@Transactional
	public List<ResCreateDongDto> createDongs(UUID sigunguId, List<ReqCreateDongDto> requestDtoList) {
		Sigungu sigungu = sigunguRepository.findById(sigunguId).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		List<String> names = requestDtoList.stream()
			.map(ReqCreateDongDto::getName)
			.toList();

		if (dongRepository.existsByNameInAndSigungu(names, sigungu)) {
			log.warn("동 지역 이름 중복");
			throw new IllegalArgumentException("이미 존재하는 동 이름이 포함되어 있습니다.");
		}

		List<String> codes = requestDtoList.stream()
			.map(ReqCreateDongDto::getCode)
			.toList();

		if (dongRepository.existsByCodeIn(codes)) {
			log.warn("동 지역 코드 중복");
			throw new IllegalArgumentException("이미 존재하는 동 코드가 포함되어 있습니다.");
		}

		List<Dong> dongList = requestDtoList.stream()
			.map(requestDto -> Dong.builder()
				.sigungu(sigungu)
				.name(requestDto.getName())
				.code(requestDto.getCode())
				.build()
			)
			.toList();

		List<Dong> savedDongList = dongRepository.saveAll(dongList);

		return savedDongList.stream()
			.map(ResCreateDongDto::from)
			.toList();
	}

	// 동 목록 조회
	public List<ResReadDongDto> getAllDong(UUID sigunguId) {
		Sigungu sigungu = sigunguRepository.findById(sigunguId).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		return dongRepository.findAllBySigungu(sigungu).stream()
			.map(ResReadDongDto::from)
			.toList();
	}

	// 동 수정
	@Transactional
	public ResUpdateDongDto updateDong(UUID sigunguId, UUID dongId, ReqCreateDongDto requestDto) {
		Sigungu sigungu = sigunguRepository.findById(sigunguId).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		Dong dong = dongRepository.findById(dongId).orElseThrow(() -> {
			log.warn("동 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 동입니다.");
		});

		if (dongRepository.existsByNameAndSigunguAndIdNot(requestDto.getName(), sigungu, dong.getId())) {
			log.warn("동 지역 이름 중복");
			throw new IllegalArgumentException("이미 존재하는 동 이름입니다.");
		}

		if (dongRepository.existsByCodeAndIdNot(requestDto.getCode(), dong.getId())) {
			log.warn("동 지역 코드 중복");
			throw new IllegalArgumentException("이미 존재하는 동 코드입니다.");
		}

		dong.update(sigungu, requestDto.getName(), requestDto.getCode());

		return ResUpdateDongDto.from(dong);
	}

	// 동 삭제
	@Transactional
	public void deleteDong(UUID sigunguId, UUID dongId, Long loginUserId) {
		Sigungu sigungu = sigunguRepository.findById(sigunguId).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		Dong dong = dongRepository.findByIdAndSigungu(dongId, sigungu).orElseThrow(() -> {
			log.warn("동 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 동입니다.");
		});

		dong.softDelete(loginUserId);
	}

}
