package com.sparta.delivery.backend.reply.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.reply.entity.Reply;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResViewReplyDto {

	private UUID replyId;
	private UUID reviewId;
	private String context;
	private String writerName;

	private Instant createdAt;
	private Long createdBy;
	private Instant updatedAt;
	private Long updatedBy;

	public static ResViewReplyDto of(Reply reply) {
		ResViewReplyDto dto = new ResViewReplyDto();
		dto.replyId = reply.getId();
		dto.reviewId = reply.getReview().getId();
		dto.context = reply.getContext();
		dto.writerName = reply.getOwner().getNickname();
		dto.createdAt = reply.getCreatedAt();
		dto.createdBy = reply.getCreatedBy();
		dto.updatedAt = reply.getUpdatedAt();
		dto.updatedBy = reply.getUpdatedBy();

		return dto;
	}

}
