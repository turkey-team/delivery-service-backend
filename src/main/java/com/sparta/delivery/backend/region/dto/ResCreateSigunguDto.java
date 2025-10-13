package com.sparta.delivery.backend.region.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Sigungu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResCreateSigunguDto {

	@Schema(description = "시·군·구 고유 ID", example = "cc1093ef-66c9-419d-a013-9718ae639a6b")
	private UUID id;

	@Schema(description = "시·도 고유 ID", example = "fba4b623-1f39-425c-98ff-fe739bfbd010")
	private UUID sidoId;

	@Schema(description = "시·군·구 이름", example = "강남구")
	private String name;

	@Schema(description = "시·군·구 코드", example = "680")
	private String code;

	@Schema(description = "시·군·구 생성 시간", example = "2025-10-09T06:51:06.579541900Z")
	private Instant createdAt;

	public static ResCreateSigunguDto from(Sigungu sigungu) {
		return new ResCreateSigunguDto(
			sigungu.getId(),
			sigungu.getSido().getId(),
			sigungu.getName(),
			sigungu.getCode(),
			sigungu.getCreatedAt()
		);
	}

}
