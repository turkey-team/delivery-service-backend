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
@ReportAsSingleViolation
@Constraint(validatedBy = {})
@Pattern(
	regexp = "^(01[016789][0-9]{7,8}|02[0-9]{7,8}|0[3-9][0-9][0-9]{6,7})$",
	message = "올바른 전화번호 형식이 아닙니다. (숫자만 입력, 예: 01012345678, 0212345678)"
)
public @interface ValidPhoneNumber {

	String message() default "올바른 전화번호 형식이 아닙니다. (숫자만 입력, 예: 01012345678, 0212345678)";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}