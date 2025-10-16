// package com.sparta.delivery.backend.address.dto;
//
// import io.swagger.v3.oas.annotations.media.Schema;
// import jakarta.validation.constraints.NotBlank;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
//
// @Getter
// @AllArgsConstructor
// public class ReqUpdateAddressDto {
//
// 	@Schema(description = "법정동 코드", example = "103", requiredMode = Schema.RequiredMode.REQUIRED)
// 	@NotBlank(message = "법정동 코드는 필수 입력 값입니다.")
// 	private String regionCode;
//
// 	@Schema(description = "주소", example = "서울특별시 마포구 망원1동 마포나루길 4", requiredMode = Schema.RequiredMode.REQUIRED)
// 	@NotBlank(message = "주소는 필수 입력 값입니다.")
// 	private String address;
// }
