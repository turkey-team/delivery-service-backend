// package com.sparta.delivery.backend.address.dto;
//
// import io.swagger.v3.oas.annotations.media.Schema;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Size;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
//
// @Getter
// @AllArgsConstructor
// public class ReqRegisterAddressDto {
//
// 	@Schema(description = "법정동 코드", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
// 	@NotBlank(message = "법정동 코드는 필수 입력 값입니다.")
// 	private String regionCode;
//
// 	@Schema(description = "주소", example = "서울특별시 마포구 망원1동 마포나루길 4", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
// 	@NotBlank(message = "주소는 필수 입력 값입니다.")
// 	@Size(min = 10, max = 255, message = "주소는 최소 10자, 최대 255자까지 가능합니다.")
// 	private String address;
// }
