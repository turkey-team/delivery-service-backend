package com.sparta.delivery.backend.region.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Sigungu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResUpdateSigunguDto {

	private UUID id;

	private UUID sidoId;

	private String name;

	private String code;

	private Instant updatedAt;

	public static ResUpdateSigunguDto from(Sigungu sigungu) {
		return new ResUpdateSigunguDto(
			sigungu.getId(),
			sigungu.getSido().getId(),
			sigungu.getName(),
			sigungu.getCode(),
			sigungu.getUpdatedAt()
		);
	}

}
