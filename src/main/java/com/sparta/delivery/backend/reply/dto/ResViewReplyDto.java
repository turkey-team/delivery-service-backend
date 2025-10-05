package com.sparta.delivery.backend.reply.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.reply.entity.Reply;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
@Schema(name = "ResViewReplyDto", description = "리뷰 답글 조회 DTO")
public class ResViewReplyDto {

	@Schema(description = "답글 ID", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID replyId;

	@Schema(description = "리뷰 ID", example = "660e8400-e29b-41d4-a716-446655440000")
	private UUID reviewId;

	@Schema(description = "답글 내용", example = "고객님 소중한 의견 감사합니다.")
	private String context;

	@Schema(description = "작성자 이름", example = "홍길동")
	private String writerName;

	@Schema(description = "답글 생성 시각", example = "2025-10-05T12:34:56.789Z")
	private Instant createdAt;

	@Schema(description = "답글 생성자 ID", example = "1")
	private Long createdBy;

	@Schema(description = "답글 수정 시각", example = "2025-10-05T12:40:00.000Z")
	private Instant updatedAt;

	@Schema(description = "답글 수정자 ID", example = "1")
	private Long updatedBy;

	public static ResViewReplyDto of(Reply reply) {
		ResViewReplyDto dto = new ResViewReplyDto();
		dto.replyId = reply.getId();
		dto.reviewId = reply.getReview().getId();
		dto.context = reply.getContext();
		dto.writerName = reply.getWriterName();
		dto.createdAt = reply.getCreatedAt();
		dto.createdBy = reply.getCreatedBy();
		dto.updatedAt = reply.getUpdatedAt();
		dto.updatedBy = reply.getUpdatedBy();

		return dto;
	}
}
