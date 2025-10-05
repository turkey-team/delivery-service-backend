package com.sparta.delivery.backend.region.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sparta.delivery.backend.global.excpetion.ApiException;

@ControllerAdvice
public class RegionExceptionHandler {

	/** 요청에 중복 값 존재 */
	@ExceptionHandler({RegionDuplicateRequestException.class})
	public ResponseEntity<ApiException> handleException(RegionDuplicateRequestException ex) {
		ApiException apiException = new ApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.BAD_REQUEST
		);
	}

	/** 엔티티 미존재 */
	@ExceptionHandler({RegionNotFoundException.class})
	public ResponseEntity<ApiException> handleException(RegionNotFoundException ex) {
		ApiException apiException = new ApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.NOT_FOUND
		);
	}

	/** DB에 중복 값 존재 */
	@ExceptionHandler({RegionAlreadyExistsException.class})
	public ResponseEntity<ApiException> handleException(RegionAlreadyExistsException ex) {
		ApiException apiException = new ApiException(ex.getMessage(), HttpStatus.CONFLICT.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.CONFLICT
		);
	}

}
