package com.sparta.delivery.backend.email.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResEmailVerificationDto {
    private String message;
    private boolean success;
    
    public static ResEmailVerificationDto success(String message) {
        return new ResEmailVerificationDto(message,true);
    }
    
    public static ResEmailVerificationDto failure(String message) {
        return new ResEmailVerificationDto(message, false);
    }
}
