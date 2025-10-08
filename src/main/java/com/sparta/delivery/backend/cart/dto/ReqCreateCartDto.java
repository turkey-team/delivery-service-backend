package com.sparta.delivery.backend.cart.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateCartDto {
	@NotBlank
	private UUID menuId;

}
