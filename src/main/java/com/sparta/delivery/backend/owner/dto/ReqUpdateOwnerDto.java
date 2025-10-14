package com.sparta.delivery.backend.owner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ReqUpdateOwnerDto", description = "점주 정보 수정 요청")
public class ReqUpdateOwnerDto {

	@Schema(description = "닉네임", example = "홍길동")
	@Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
	private String nickname;
}
