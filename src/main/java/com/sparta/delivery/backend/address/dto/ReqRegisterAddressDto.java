package com.sparta.delivery.backend.address.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ReqRegisterAddressDto {

	@NotBlank(message = "법정동 코드는 필수 입력 값입니다.")
	private String regionCode;

	@NotBlank(message = "주소는 필수 입력 값입니다.")
	private String address;
}
