package com.sparta.delivery.backend.region.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqCreateDongDto {

	@NotBlank(message = "동 이름은 필수입니다.")
	@Size(max = 50, message = "동 이름은 최대 50자까지 가능합니다.")
	private String name;

	@NotBlank(message = "동 코드는 필수입니다.")
	@Size(max = 3, message = "동 코드는 최대 3자까지 가능합니다.")
	private String code;

}
