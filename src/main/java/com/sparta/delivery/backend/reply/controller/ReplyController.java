package com.sparta.delivery.backend.reply.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.reply.dto.ReqCreateReplyDto;
import com.sparta.delivery.backend.reply.dto.ReqUpdateReplyDto;
import com.sparta.delivery.backend.reply.dto.ResDeleteReplyDto;
import com.sparta.delivery.backend.reply.dto.ResViewReplyDto;
import com.sparta.delivery.backend.reply.service.ReplyService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/replies")
@Tag(name = "Reply-Controller", description = "리뷰 답글 관련 API")
public class ReplyController {

	private final ReplyService replyService;

	@Operation(summary = "리뷰 답글 목록 조회",
		description = "주어진 리뷰 ID에 달린 모든 답글을 조회합니다. OWNER, MANAGER 권한 필요")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "답글 조회 성공", content = @Content(
			array = @ArraySchema(schema = @Schema(implementation = ResViewReplyDto.class)))),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
	})
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@GetMapping("/review/{reviewId}")
	public List<ResViewReplyDto> getReplies(
		@Parameter(description = "조회할 리뷰 ID", required = true) @PathVariable UUID reviewId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long userId = userDetails.getId();
		return replyService.getReplies(reviewId, userId);
	}

	@Operation(summary = "리뷰 답글 작성",
		description = "주어진 리뷰 ID에 답글을 작성합니다. OWNER, MANAGER 권한 필요")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "답글 작성 성공", content = @Content(
			schema = @Schema(implementation = ResViewReplyDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
	})
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@PostMapping("/review/{reviewId}")
	public ResViewReplyDto writeReply(
		@Parameter(description = "작성할 답글 DTO", required = true)
		@Valid @RequestBody ReqCreateReplyDto dto,
		@Parameter(description = "답글 대상 리뷰 ID", required = true)
		@PathVariable UUID reviewId,
		@Parameter(hidden = true)
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long userId = userDetails.getId();
		return replyService.createReply(dto, reviewId, userId);
	}

	@Operation(summary = "리뷰 답글 수정",
		description = "주어진 답글 ID에 대해 답글 내용을 수정합니다. OWNER, MANAGER 권한 필요")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "답글 수정 성공", content = @Content(
			schema = @Schema(implementation = ResViewReplyDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "답글을 찾을 수 없음")
	})
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@PutMapping("/{replyId}")
	public ResViewReplyDto updateReply(
		@Parameter(description = "수정할 답글 DTO", required = true)
		@Valid @RequestBody ReqUpdateReplyDto dto,
		@Parameter(description = "수정할 답글 ID", required = true)
		@PathVariable UUID replyId,
		@Parameter(hidden = true)
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long userId = userDetails.getId();
		return replyService.updateReply(dto, replyId, userId);
	}

	@Operation(summary = "리뷰 답글 삭제",
		description = "주어진 답글 ID를 삭제합니다. OWNER, MANAGER 권한 필요")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "답글 삭제 성공", content = @Content(
			schema = @Schema(implementation = ResDeleteReplyDto.class))),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "답글을 찾을 수 없음")
	})
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@DeleteMapping("/{replyId}")
	public ResDeleteReplyDto deleteReply(
		@Parameter(description = "삭제할 답글 ID", required = true)
		@PathVariable UUID replyId,
		@Parameter(hidden = true)
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long userId = userDetails.getId();
		return replyService.deleteReply(replyId, userId);
	}

}
