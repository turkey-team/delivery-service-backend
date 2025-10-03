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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/replies")
public class ReplyController {

	private final ReplyService replyService;

	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@GetMapping("/review/{reviewId}")
	public List<ResViewReplyDto> getReplies(@PathVariable UUID reviewId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long userId = userDetails.getId();
		return replyService.getReplies(reviewId, userId);
	}

	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@PostMapping("/review/{reviewId}")
	public ResViewReplyDto writeReply(@Valid @RequestBody ReqCreateReplyDto dto,
		@PathVariable UUID reviewId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long userId = userDetails.getId();
		return replyService.createReply(dto, reviewId, userId);
	}

	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@PutMapping("/{replyId}")
	public ResViewReplyDto updateReply(@Valid @RequestBody ReqUpdateReplyDto dto,
		@PathVariable UUID replyId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long userId = userDetails.getId();
		return replyService.updateReply(dto, replyId, userId);
	}

	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@DeleteMapping("/{replyId}")
	public ResDeleteReplyDto deleteReply(@PathVariable UUID replyId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long userId = userDetails.getId();
		return replyService.deleteReply(replyId, userId);
	}

}
