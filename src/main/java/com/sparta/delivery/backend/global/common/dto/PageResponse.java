package com.sparta.delivery.backend.global.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageResponse<T> {

	private List<T> content;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean first;
	private boolean last;
	private boolean hasNext;

	public static <T> PageResponse<T> of(Page<T> page) {
		return new PageResponse<>(
			page.getContent(),
			page.getNumber(),
			page.getSize(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.isFirst(),
			page.isLast(),
			page.hasNext()
		);
	}
}
