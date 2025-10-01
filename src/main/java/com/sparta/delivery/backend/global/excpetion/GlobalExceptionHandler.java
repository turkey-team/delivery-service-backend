package com.sparta.delivery.backend.global.excpetion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({UnauthorizedException.class})
	public ResponseEntity<ApiException> handleException(IllegalArgumentException ex) {
		ApiException apiException = new ApiException(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.UNAUTHORIZED
		);
	}

	@ExceptionHandler({DuplicateUsernameException.class})
	public ResponseEntity<ApiException> handleException(DuplicateUsernameException ex) {
		ApiException apiException = new ApiException(ex.getMessage(), HttpStatus.CONFLICT.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.CONFLICT
		);
	}
}
