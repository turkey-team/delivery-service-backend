package com.sparta.delivery.backend.region.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Dong;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResUpdateDongDto {

	private UUID id;

	private UUID sigunguId;

	private String name;

	private String code;

	private Instant updatedAt;

	public static ResUpdateDongDto from(Dong dong) {
		return new ResUpdateDongDto(
			dong.getId(),
			dong.getSigungu().getId(),
			dong.getName(),
			dong.getCode(),
			dong.getUpdatedAt()
		);
	}

}
