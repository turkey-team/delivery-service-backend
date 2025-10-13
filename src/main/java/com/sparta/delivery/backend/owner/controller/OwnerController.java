package com.sparta.delivery.backend.owner.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.owner.dto.ReqChangePasswordDto;
import com.sparta.delivery.backend.owner.dto.ReqCreateOwnerDto;
import com.sparta.delivery.backend.owner.dto.ReqPasswordResetDto;
import com.sparta.delivery.backend.owner.dto.ReqPasswordResetRequestDto;
import com.sparta.delivery.backend.owner.dto.ReqUpdateOwnerDto;
import com.sparta.delivery.backend.owner.dto.ResGetMyOwnerDto;
import com.sparta.delivery.backend.owner.dto.ResGetOwnerDto;
import com.sparta.delivery.backend.owner.dto.ResPasswordResetRequestDto;
import com.sparta.delivery.backend.owner.service.OwnerService;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/owners")
@RequiredArgsConstructor
@Tag(name = "점주 API V1", description = "점주 관련 API")
public class OwnerController {

    private final OwnerService ownerService;

    @Operation(summary = "점주 회원가입", description = "새로운 점주를 등록합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자")
    })
    @PostMapping
    public ResponseEntity<Void> createOwner(@Valid @RequestBody ReqCreateOwnerDto requestDto) {
       ownerService.createOwner(requestDto);
       return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "내 정보 조회", description = "로그인한 점주의 마이페이지 정보를 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "점주 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @Secured(UserRoleEnum.Authority.OWNER)
    @GetMapping("/me")
    public ResponseEntity<ResGetMyOwnerDto> getCurrentOwner(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ResGetMyOwnerDto response = ownerService.getCurrentOwner(userDetails);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "점주 정보 조회 (관리자용)", description = "관리자가 특정 점주의 정보를 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "점주 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @Secured(UserRoleEnum.Authority.MANAGER)
    @GetMapping("/{ownerUserPublicId}")
    public ResponseEntity<ResGetOwnerDto> getOwnerByUserPublicId(@PathVariable UUID ownerUserPublicId) {
        ResGetOwnerDto response = ownerService.getOwnerByUserPublicId(ownerUserPublicId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 정보 수정", description = "로그인한 점주의 정보를 수정합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "점주 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @Secured(UserRoleEnum.Authority.OWNER)
    @PatchMapping("/me")
    public ResponseEntity<Void> updateCurrentOwner(
        @AuthenticationPrincipal UserDetailsImpl userDetails, 
        @Valid @RequestBody ReqUpdateOwnerDto requestDto) {
        ownerService.updateCurrentOwner(userDetails, requestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경", description = "로그인한 상태에서 비밀번호를 변경합니다 (기존 비밀번호 필요)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "점주 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @Secured(UserRoleEnum.Authority.OWNER)
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody ReqChangePasswordDto requestDto) {
        ownerService.changePassword(userDetails, requestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 재설정 요청", description = "이메일로 비밀번호 재설정 링크를 발송합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청이 처리되었습니다"),
    })
    @PostMapping("/password-reset/request")
    public ResponseEntity<ResPasswordResetRequestDto> requestPasswordReset(
        @Valid @RequestBody ReqPasswordResetRequestDto requestDto) {
        ownerService.requestPasswordReset(requestDto);
        return ResponseEntity.ok(new ResPasswordResetRequestDto(
            "해당 이메일로 가입된 계정이 있다면 비밀번호 재설정 링크가 발송됩니다."
        ));
    }

    @Operation(summary = "비밀번호 재설정 확인", description = "토큰을 사용하여 새 비밀번호로 변경합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "비밀번호가 성공적으로 변경되었습니다"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content())
    })
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ReqPasswordResetDto requestDto) {
        ownerService.resetPassword(requestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 계정 탈퇴", description = "로그인한 점주가 본인 계정을 탈퇴합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @Secured(UserRoleEnum.Authority.OWNER)
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentOwner(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ownerService.deleteCurrentOwner(userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "점주 탈퇴 (관리자용)", description = "관리자가 특정 점주를 탈퇴 처리합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "탈퇴 처리 완료"),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "점주를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @Secured(UserRoleEnum.Authority.MANAGER)
    @DeleteMapping("/{ownerUserPublicId}")
    public ResponseEntity<Void> deleteOwnerByManager(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID ownerUserPublicId) {
        ownerService.deleteOwnerByManager(userDetails, ownerUserPublicId);
        return ResponseEntity.ok().build();
    }
}
