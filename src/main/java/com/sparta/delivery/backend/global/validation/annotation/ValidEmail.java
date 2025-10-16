package com.sparta.delivery.backend.global.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@Pattern(
	regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
	message = "올바른 이메일 형식이 아닙니다. (ex. user@example.com)"
)
public @interface ValidEmail {

	String message() default "올바른 이메일 형식이 아닙니다. (ex. user@example.com)";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}