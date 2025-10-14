package com.sparta.delivery.backend.global.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotBlank(message = "비밀번호는 필수입니다.")
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,15}$", message = "비밀번호는 8~15자이며, 대소문자, 숫자, 특수문자를 모두 포함해야 합니다.")
public @interface ValidPassword {
	String message() default "비밀번호는 8~15자이며, 대소문자, 숫자, 특수문자를 모두 포함해야 합니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}