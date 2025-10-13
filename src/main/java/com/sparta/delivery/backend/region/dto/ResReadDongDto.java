package com.sparta.delivery.backend.region.dto;

import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Dong;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResReadDongDto {

	@Schema(description = "동 고유 ID", example = "c373ba20-9e15-41fd-acdd-b180aba31b12")
	private UUID id;

	@Schema(description = "동 이름", example = "역삼동")
	private String name;

	@Schema(description = "동 코드", example = "010")
	private String code;

	public static ResReadDongDto from(Dong dong) {
		return new ResReadDongDto(
			dong.getId(),
			dong.getName(),
			dong.getCode()
		);
	}

}
