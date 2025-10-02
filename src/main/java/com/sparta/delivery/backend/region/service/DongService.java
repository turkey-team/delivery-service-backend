package com.sparta.delivery.backend.region.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.region.dto.ReqCreateDongDto;
import com.sparta.delivery.backend.region.dto.ResCreateDongDto;
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
	public ResCreateDongDto createDong(UUID sigunguId, ReqCreateDongDto requestDto) {
		Sigungu sigungu = sigunguRepository.findById(sigunguId).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		if (dongRepository.existsByNameAndSigungu(requestDto.getName(), sigungu)) {
			log.warn("동 지역 이름 중복");
			throw new IllegalArgumentException("이미 존재하는 동 이름입니다.");
		}

		if (dongRepository.existsByCode(requestDto.getCode())) {
			log.warn("동 지역 코드 중복");
			throw new IllegalArgumentException("이미 존재하는 동 코드입니다.");
		}

		Dong dong = Dong.builder()
			.sigungu(sigungu)
			.name(requestDto.getName())
			.code(requestDto.getCode())
			.build();

		Dong savedDong = dongRepository.save(dong);

		return ResCreateDongDto.from(savedDong);
	}

}
