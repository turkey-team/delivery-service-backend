package com.sparta.delivery.backend.global.excpetion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({UnauthorizedException.class})
	public ResponseEntity<ApiException> handleException(UnauthorizedException ex) {
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

	@ExceptionHandler({HttpMessageNotReadableException.class})
	public ResponseEntity<ApiException> handleException(HttpMessageNotReadableException ex) {
		ApiException apiException = new ApiException("잘못된 JSON 형식입니다.", HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<ApiException> handleException(MethodArgumentNotValidException ex) {
		String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
		ApiException apiException = new ApiException(errorMessage, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler({HandlerMethodValidationException.class})
	public ResponseEntity<ApiException> handleException(HandlerMethodValidationException ex) {
		String errorMessage = ex.getAllErrors().get(0).getDefaultMessage();
		ApiException apiException = new ApiException(errorMessage, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.BAD_REQUEST
		);
	}
}
