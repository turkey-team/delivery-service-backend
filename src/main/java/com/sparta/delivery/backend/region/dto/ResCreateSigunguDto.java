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
public class ResCreateSigunguDto {

	private UUID id;

	private String name;

	private String code;

	private Instant createdAt;

	public static ResCreateSigunguDto from(Sigungu sigungu) {
		return new ResCreateSigunguDto(
			sigungu.getId(),
			sigungu.getName(),
			sigungu.getCode(),
			sigungu.getCreatedAt()
		);
	}

}
