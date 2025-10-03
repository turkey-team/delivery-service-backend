package com.sparta.delivery.backend.reply.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.reply.entity.Reply;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ResDeleteReplyDto {

	private UUID replyId;
	private UUID reviewId;
	private String context;
	private String writerName;

	private Instant createdAt;
	private Long createdBy;
	private Instant deletedAt;
	private Long deletedBy;

	public static ResDeleteReplyDto of(Reply reply) {
		ResDeleteReplyDto dto = new ResDeleteReplyDto();
		dto.replyId = reply.getId();
		dto.reviewId = reply.getReview().getId();
		dto.context = reply.getContext();
		dto.writerName = reply.getWriterName();
		dto.createdAt = reply.getCreatedAt();
		dto.createdBy = reply.getCreatedBy();
		dto.deletedAt = reply.getDeletedAt();
		dto.deletedBy = reply.getDeletedBy();

		return dto;
	}

}
