package com.sparta.delivery.backend.reply.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.global.excpetion.UnauthorizedException;
import com.sparta.delivery.backend.manager.entity.Manager;
import com.sparta.delivery.backend.manager.repository.ManagerRepository;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyService {

	private final ReplyRepository replyRepository;
	private final ReviewRepository reviewRepository;
	private final OwnerRepository ownerRepository;
	private final ManagerRepository managerRepository;

	private final ChatClient chatClient;

	private static final int MAX_RETRY = 3;

	@Async
	public void generateReplyAsync(UUID reviewId, UUID ownerId) {
		log.info("generateReplyAsync 시작 - thread: {}", Thread.currentThread().getName());
		createReplyTransactionalWithRetry(reviewId, ownerId);
	}

	@Transactional
	public void createReplyTransactionalWithRetry(UUID reviewId, UUID ownerId) {
		int attempt = 0;

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new NoSuchElementException("리뷰 없음"));

		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new NoSuchElementException("점주 없음"));

		while (attempt < MAX_RETRY) {
			try {
				// 이미 답글 존재 시 중복 방지
				if (replyRepository.existsByReviewId(review.getId())) {
					log.info("이미 답글 존재 - reviewId: {}", review.getId());
					return;
				}

				// AI 호출
				String autoreplyContext = chatClient.prompt()
					.user("""
						다음 고객 리뷰에 대해 점주의 입장에서 공손하고
						친절한 답글을 작성해주세요.
						너무 공통적인 답글보다는 리뷰에 맞는 답글이면 좋겠습니다.
						너무 길지 않게 (500글자 이내) 답변해주세요.
						고객 리뷰: %s
						""".formatted(review.getContext()))
					.call().content();

				// 답글 저장
				Reply reply = Reply.builder()
					.context(autoreplyContext)
					.review(review)
					.owner(owner)
					.build();

				replyRepository.save(reply);
				log.info("AI 답글 생성 성공 - reviewId: {}", review.getId());
				break;

			} catch (Exception e) {
				attempt++;
				log.error("AI 답글 생성 실패 ({}회차) - reviewId: {}", attempt, review.getId(), e);

				if (attempt >= MAX_RETRY) {
					log.error("최종 실패 - reviewId: {}", review.getId());
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						log.error("재시도 중 인터럽트 발생", ie);
					}
				}
			}
		}
	}

	// 답글 조회
	@Transactional(readOnly = true)
	public List<ResViewReplyDto> getReplies(UUID reviewId, Long userId) {
		Owner owner = ownerRepository.findByUserId(userId).orElse(null);
		Manager manager = managerRepository.findByUserId(userId).orElse(null);

		if (owner == null && manager == null) {
			throw new UnauthorizedException("리뷰 답글을 조회할 권한이 없습니다.");
		}

		return replyRepository.findByReviewId(reviewId)
			.stream()
			.map(ResViewReplyDto::of)
			.collect(Collectors.toList());
	}

	// 답글 작성
	@Transactional
	public ResViewReplyDto createReply(ReqCreateReplyDto createReplyDto, UUID reviewId, Long userId) {
		// 1. 리뷰 조회
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new NoSuchElementException("해당 리뷰를 찾을 수 없습니다."));

		// 2. 작성자 조회
		Owner owner = ownerRepository.findByUserId(userId).orElse(null);
		Manager manager = managerRepository.findByUserId(userId).orElse(null);

		if (owner == null && manager == null) {
			throw new UnauthorizedException("리뷰 답글을 등록할 권한이 없습니다.");
		}

		// Owner가 존재할 때만 추가 검증
		if (owner != null) {
			if (!review.getStore().getOwner().getId().equals(owner.getId())) {
				throw new UnauthorizedException("해당 가게의 점주만 리뷰 답글을 등록할 수 있습니다.");
			}
		}

		Reply reply = Reply.builder()
			.context(createReplyDto.getContext())
			.review(review)
			.owner(owner)
			.manager(manager)
			.build();

		Reply saved = replyRepository.save(reply);
		return ResViewReplyDto.of(saved);
	}

	// 답글 수정
	@Transactional
	public ResViewReplyDto updateReply(ReqUpdateReplyDto updateReplyDto, UUID replyId,
		Long userId) {
		// 1. 답글 조회
		Reply reply = replyRepository.findById(replyId).orElseThrow(
			() -> new NoSuchElementException("해당 답글을 찾을 수 없습니다.")
		);

		// 2. 작성자 조회
		Owner owner = ownerRepository.findByUserId(userId).orElse(null);
		Manager manager = managerRepository.findByUserId(userId).orElse(null);

		if (owner == null && manager == null) {
			throw new UnauthorizedException("답글을 수정할 권한이 없습니다.");
		}

		if (owner != null) {
			if (!reply.getOwner().getId().equals(owner.getId())) {
				throw new UnauthorizedException("해당 가게의 점주만 리뷰 답글을 수정할 수 있습니다.");
			}
		}

		reply.update(updateReplyDto.getContext());

		return ResViewReplyDto.of(reply);
	}

	// 답글 삭제
	@Transactional
	public ResDeleteReplyDto deleteReply(UUID replyId, Long userId) {
		// 1. 답글 조회
		Reply reply = replyRepository.findById(replyId).orElseThrow(
			() -> new NoSuchElementException("해당 답글을 찾을 수 없습니다.")
		);

		// 2. 작성자 조회
		Owner owner = ownerRepository.findByUserId(userId).orElse(null);
		Manager manager = managerRepository.findByUserId(userId).orElse(null);

		if (owner == null && manager == null) {
			throw new UnauthorizedException("답글을 삭제할 권한이 없습니다.");
		}

		if (owner != null) {
			if (!reply.getOwner().getId().equals(owner.getId())) {
				throw new UnauthorizedException("해당 가게의 점주만 리뷰 답글을 삭제할 수 있습니다.");
			}
		}

		reply.softDelete(userId);

		return ResDeleteReplyDto.of(reply);
	}

}
