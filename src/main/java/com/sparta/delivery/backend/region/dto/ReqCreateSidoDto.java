package com.sparta.delivery.backend.region.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqCreateSidoDto {

	@Schema(description = "시·도 이름", example = "서울특별시")
	@NotBlank(message = "시/도 이름은 필수입니다.")
	@Pattern(regexp = "^[가-힣0-9\\-]{1,50}$", message = "시/도 이름은 한글, 숫자, 하이픈(-)만 사용할 수 있으며, 최대 50자까지 가능합니다.")
	private String name;

	@Schema(description = "시·도 코드", example = "11")
	@NotBlank(message = "시/도 코드는 필수입니다.")
	@Pattern(regexp = "^[0-9]{2}$", message = "시/도 코드는 2자의 숫자만 가능합니다.")
	private String code;

}
