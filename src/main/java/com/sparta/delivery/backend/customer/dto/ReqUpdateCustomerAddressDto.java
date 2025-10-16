package com.sparta.delivery.backend.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqUpdateCustomerAddressDto {

	@Schema(description = "지역 코드 (읍면동 코드)", example = "1111010300")
	@Size(min = 10, max = 10, message = "지역 코드는 10자리여야 합니다.")
	@NotBlank(message = "법정동 코드는 필수 입력 값입니다.")
	private String regionCode;

	@Schema(description = "전체 주소", example = "서울특별시 종로구 돈화문로 27")
	@NotBlank(message = "주소는 필수 입력 값입니다.")
	@Size(min = 10, max = 255, message = "주소는 최소 10자, 최대 255자까지 가능합니다.")
	private String fullAddress;

	@Schema(description = "배송지 별명", example = "우리집")
	@NotBlank(message = "별명은 필수 입력 값입니다.")
	@Size(max = 50, message = "별명은 최대 50자까지 가능합니다.")
	private String nickname;

	@Schema(description = "기본 배송지 설정 여부", example = "true")
	private Boolean isDefault = false;

	@DecimalMin(value = "-180.0", message = "경도는 -180 이상 ~ 180 이하이어야 합니다.")
	@DecimalMax(value = "180.0", message = "경도는 -180 이상 ~ 180 이하이어야 합니다.")
	@NotNull(message = "경도는 필수입니다.")
	private Double longitude;

	@DecimalMin(value = "-90.0", message = "위도는 -90 이상 ~ 90 이하이어야 합니다.")
	@DecimalMax(value = "90.0", message = "위도는 -90 이상 ~ 90 이하이어야 합니다.")
	@NotNull(message = "위도는 필수입니다.")
	private Double latitude;
}
