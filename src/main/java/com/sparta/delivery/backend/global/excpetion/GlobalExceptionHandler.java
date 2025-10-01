package com.sparta.delivery.backend.global.excpetion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

	@ExceptionHandler({NotFoundException.class})
	public ResponseEntity<ApiException> handleException(NotFoundException ex) {
		ApiException apiException = new ApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.NOT_FOUND
		);
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<ApiException> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		ApiException apiException = new ApiException("필수 파라미터가 없거나 검증에 실패했습니다.", HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.BAD_REQUEST
		);
	}
}
