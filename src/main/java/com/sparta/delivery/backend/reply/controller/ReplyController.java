package com.sparta.delivery.backend.reply.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/replies")
public class ReplyController {

	private final ReplyService replyService;

	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@GetMapping("/review/{reviewId}")
	public List<ResViewReplyDto> getReplies(@PathVariable UUID reviewId) {
		return replyService.getReplies(reviewId);
	}

	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@PostMapping("/review/{reviewId}")
	public ResViewReplyDto writeReply(@RequestBody ReqCreateReplyDto dto, @PathVariable UUID reviewId) {
		return replyService.createReply(dto, reviewId);
	}

	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@PutMapping("/{replyId}")
	public ResViewReplyDto updateReply(@RequestBody ReqUpdateReplyDto dto, @PathVariable UUID replyId) {
		return replyService.updateReply(dto, replyId);
	}

	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@DeleteMapping("/{replyId}")
	public ResDeleteReplyDto deleteReply(@PathVariable UUID replyId) {
		return replyService.deleteReply(replyId);
	}

}
