package com.sparta.delivery.backend.global.excpetion;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.sparta.delivery.backend.global.infra.slack.SlackService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final Optional<SlackService> slackService;

	@ExceptionHandler({UnauthorizedException.class})
	public ResponseEntity<ApiException> handleException(UnauthorizedException ex, HttpServletRequest request) {
		String errorMessage = ex.getMessage();
		slackService.ifPresent(service ->
			service.sendMessage(request.getRequestURI(), ex.getClass().getSimpleName(), errorMessage)
		);

		ApiException apiException = new ApiException(errorMessage, HttpStatus.UNAUTHORIZED.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.UNAUTHORIZED
		);
	}

	@ExceptionHandler({IllegalArgumentException.class})
	public ResponseEntity<ApiException> handleException(IllegalArgumentException ex, HttpServletRequest request) {
		String errorMessage = ex.getMessage();
		slackService.ifPresent(service ->
			service.sendMessage(request.getRequestURI(), ex.getClass().getSimpleName(), errorMessage)
		);

		ApiException apiException = new ApiException(errorMessage, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler({DuplicateUsernameException.class})
	public ResponseEntity<ApiException> handleException(DuplicateUsernameException ex, HttpServletRequest request) {
		String errorMessage = ex.getMessage();
		slackService.ifPresent(service ->
			service.sendMessage(request.getRequestURI(), ex.getClass().getSimpleName(), errorMessage)
		);

		ApiException apiException = new ApiException(errorMessage, HttpStatus.CONFLICT.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.CONFLICT
		);
	}

	@ExceptionHandler({NotFoundException.class})
	public ResponseEntity<ApiException> handleException(NotFoundException ex, HttpServletRequest request) {
		String errorMessage = ex.getMessage();
		slackService.ifPresent(service ->
			service.sendMessage(request.getRequestURI(), ex.getClass().getSimpleName(), errorMessage)
		);

		ApiException apiException = new ApiException(errorMessage, HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.NOT_FOUND
		);
	}

	@ExceptionHandler({HttpMessageNotReadableException.class})
	public ResponseEntity<ApiException> handleException(HttpMessageNotReadableException ex,
		HttpServletRequest request) {
		String errorMessage = "잘못된 JSON 형식입니다.";
		slackService.ifPresent(service ->
			service.sendMessage(request.getRequestURI(), ex.getClass().getSimpleName(), errorMessage)
		);

		ApiException apiException = new ApiException(errorMessage, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<ApiException> handleException(MethodArgumentNotValidException ex,
		HttpServletRequest request) {
		String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
		slackService.ifPresent(service ->
			service.sendMessage(request.getRequestURI(), ex.getClass().getSimpleName(), errorMessage)
		);

		ApiException apiException = new ApiException(errorMessage, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler({HandlerMethodValidationException.class})
	public ResponseEntity<ApiException> handleException(HandlerMethodValidationException ex,
		HttpServletRequest request) {
		String errorMessage = ex.getAllErrors().get(0).getDefaultMessage();
		slackService.ifPresent(service ->
			service.sendMessage(request.getRequestURI(), ex.getClass().getSimpleName(), errorMessage)
		);

		ApiException apiException = new ApiException(errorMessage, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler({AccessDeniedException.class})
	public ResponseEntity<ApiException> handleException(AccessDeniedException ex, HttpServletRequest request) {
		String errorMessage = "권한이 없습니다.";
		slackService.ifPresent(service ->
			service.sendMessage(request.getRequestURI(), ex.getClass().getSimpleName(), errorMessage)
		);

		ApiException apiException = new ApiException(errorMessage, HttpStatus.FORBIDDEN.value());
		return new ResponseEntity<>(
			apiException,
			HttpStatus.FORBIDDEN
		);
	}
}
