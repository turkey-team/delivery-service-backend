package com.sparta.delivery.backend.region.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqUpdateSigunguDto {

	@NotBlank(message = "시/군/구 이름은 필수입니다.")
	@Pattern(regexp = "^[가-힣0-9\\-]{1,50}$", message = "시/군/구 이름은 한글, 숫자, 하이픈(-)만 사용할 수 있으며, 최대 50자까지 가능합니다.")
	private String name;

	@NotBlank(message = "시/군/구 코드는 필수입니다.")
	@Pattern(regexp = "^[0-9]{3}$", message = "시/군/구 코드는 3자의 숫자만 가능합니다.")
	private String code;

}
