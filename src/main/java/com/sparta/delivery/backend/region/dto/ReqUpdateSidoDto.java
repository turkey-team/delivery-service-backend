package com.sparta.delivery.backend.region.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqUpdateSidoDto {

	@NotBlank(message = "시/도 이름은 필수입니다.")
	@Size(max = 50, message = "시/도 이름은 최대 50자까지 가능합니다.")
	private String name;

	@NotBlank(message = "시/도 코드는 필수입니다.")
	@Size(max = 2, message = "시/도 코드는 최대 2자까지 가능합니다.")
	private String code;

}
