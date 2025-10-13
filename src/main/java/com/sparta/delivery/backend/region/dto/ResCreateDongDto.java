package com.sparta.delivery.backend.region.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Dong;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResCreateDongDto {

	@Schema(description = "동 고유 ID", example = "c373ba20-9e15-41fd-acdd-b180aba31b12")
	private UUID id;

	@Schema(description = "시·군·구 고유 ID", example = "cc1093ef-66c9-419d-a013-9718ae639a6b")
	private UUID sigunguId;

	@Schema(description = "동 이름", example = "역삼동")
	private String name;

	@Schema(description = "동 코드", example = "010")
	private String code;

	@Schema(description = "동 생성 시간", example = "2025-10-09T06:56:18.749259900Z")
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
