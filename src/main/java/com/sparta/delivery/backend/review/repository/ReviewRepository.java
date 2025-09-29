package com.sparta.delivery.backend.review.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
}
