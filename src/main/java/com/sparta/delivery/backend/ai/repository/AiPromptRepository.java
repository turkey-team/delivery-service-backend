package com.sparta.delivery.backend.ai.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.ai.entity.AiPrompt;

public interface AiPromptRepository extends JpaRepository<AiPrompt, UUID> {
}
