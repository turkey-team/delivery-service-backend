package com.sparta.delivery.backend.region.dto;

import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Dong;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResReadDongDto {

	private UUID id;

	private String name;

	private String code;

	public static ResReadDongDto from(Dong dong) {
		return new ResReadDongDto(
			dong.getId(),
			dong.getName(),
			dong.getCode()
		);
	}

}
