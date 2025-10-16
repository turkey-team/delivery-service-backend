package com.sparta.delivery.backend.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ReqUpdateCustomerDto", description = "고객 정보 수정 요청")
public class ReqUpdateCustomerDto {

	@Schema(description = "닉네임", example = "홍길동")
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
	private String nickname;
}