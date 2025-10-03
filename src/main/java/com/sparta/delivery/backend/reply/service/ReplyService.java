package com.sparta.delivery.backend.reply.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.global.excpetion.UnauthorizedException;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.reply.dto.ReqCreateReplyDto;
import com.sparta.delivery.backend.reply.dto.ReqUpdateReplyDto;
import com.sparta.delivery.backend.reply.dto.ResDeleteReplyDto;
import com.sparta.delivery.backend.reply.dto.ResViewReplyDto;
import com.sparta.delivery.backend.reply.entity.Reply;
import com.sparta.delivery.backend.reply.repository.ReplyRepository;
import com.sparta.delivery.backend.review.entity.Review;
import com.sparta.delivery.backend.review.repository.ReviewRepository;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyService {

	private final ReplyRepository replyRepository;
	private final ReviewRepository reviewRepository;
	private final OwnerRepository ownerRepository;

	private Long getAuthenticationUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			throw new IllegalStateException("로그인 정보가 없습니다.");
		}

		UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
		Long userId = userDetails.getId();
		System.out.println("userId = " + userId);

		return userId;
	}

	// 답글 조회
	@Transactional(readOnly = true)
	public List<ResViewReplyDto> getReplies(UUID reviewId) {
		return replyRepository.findByReviewId(reviewId)
			.stream()
			.map(ResViewReplyDto::of)
			.collect(Collectors.toList());
	}

	// 답글 작성
	@Transactional
	public ResViewReplyDto createReply(ReqCreateReplyDto createReplyDto, UUID reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new NoSuchElementException("해당 리뷰를 찾을 수 없습니다."));

		Long userId = getAuthenticationUserId();
		Owner owner = ownerRepository.findByUserId(userId)
			.orElseThrow(() -> new IllegalStateException("해당 User를 찾을 수 없습니다."));

		Reply reply = Reply.builder()
			.context(createReplyDto.getContext())
			.review(review)
			.owner(owner)
			.build();

		if (!reply.getOwner().getId().equals(owner.getId())) {
			throw new UnauthorizedException("해당 가게의 점주만 리뷰 답글을 등록할 수 있습니다.");
		}

		Reply saved = replyRepository.save(reply);
		return ResViewReplyDto.of(saved);
	}

	// 답글 수정
	@Transactional
	public ResViewReplyDto updateReply(ReqUpdateReplyDto updateReplyDto, UUID replyId) {
		Reply reply = replyRepository.findById(replyId).orElseThrow(
			() -> new NoSuchElementException("해당 답글을 찾을 수 없습니다.")
		);

		Long userId = getAuthenticationUserId();
		Owner owner = ownerRepository.findByUserId(userId)
			.orElseThrow(() -> new IllegalStateException("해당 User를 찾을 수 없습니다."));

		if (!reply.getOwner().getId().equals(owner.getId())) {
			throw new UnauthorizedException("해당 가게의 점주만 리뷰 답글을 수정할 수 있습니다.");
		}

		reply.update(updateReplyDto.getContext());

		return ResViewReplyDto.of(reply);
	}

	// 답글 삭제
	@Transactional
	public ResDeleteReplyDto deleteReply(UUID replyId) {
		Reply reply = replyRepository.findById(replyId).orElseThrow(
			() -> new NoSuchElementException("해당 답글을 찾을 수 없습니다.")
		);

		Long userId = getAuthenticationUserId();
		Owner owner = ownerRepository.findByUserId(userId)
			.orElseThrow(() -> new IllegalStateException("해당 User를 찾을 수 없습니다."));

		if (!reply.getOwner().getId().equals(owner.getId())) {
			throw new UnauthorizedException("해당 가게의 점주만 리뷰 답글을 삭제할 수 있습니다.");
		}

		reply.softDelete(userId);

		return ResDeleteReplyDto.of(reply);
	}

}
