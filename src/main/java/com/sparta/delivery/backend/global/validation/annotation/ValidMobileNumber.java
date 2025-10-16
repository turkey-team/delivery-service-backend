package com.sparta.delivery.backend.global.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@Pattern(
	regexp = "^01[016789][0-9]{7,8}$",
	message = "올바른 휴대폰 번호 형식이 아닙니다. (숫자만 입력, 예: 01012345678)"
)
public @interface ValidMobileNumber {

	String message() default "올바른 휴대폰 번호 형식이 아닙니다. (숫자만 입력, 예: 01012345678)";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
