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
public class ResCreateDongDto {

	private UUID id;

	private UUID sigunguId;

	private String name;

	private String code;

	private Instant createdAt;

	public static ResCreateDongDto from(Dong dong) {
		return new ResCreateDongDto(
			dong.getId(),
			dong.getSigungu().getId(),
			dong.getName(),
			dong.getCode(),
			dong.getCreatedAt()
		);
	}

}
