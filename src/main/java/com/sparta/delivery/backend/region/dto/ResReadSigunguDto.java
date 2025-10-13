package com.sparta.delivery.backend.region.dto;

import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Sigungu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResReadSigunguDto {

	@Schema(description = "시·군·구 고유 ID", example = "cc1093ef-66c9-419d-a013-9718ae639a6b")
	private UUID id;

	@Schema(description = "시·군·구 이름", example = "강남구")
	private String name;

	@Schema(description = "시·군·구 코드", example = "680")
	private String code;

	public static ResReadSigunguDto from(Sigungu Sigungu) {
		return new ResReadSigunguDto(
			Sigungu.getId(),
			Sigungu.getName(),
			Sigungu.getCode()
		);
	}

}
