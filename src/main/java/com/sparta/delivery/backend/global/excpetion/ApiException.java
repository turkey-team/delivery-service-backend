package com.sparta.delivery.backend.global.excpetion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiException {
	private String errorMessage;
	private int statusCode;
}
