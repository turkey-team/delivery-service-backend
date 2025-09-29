package com.sparta.delivery.backend.image.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, UUID> {
}
