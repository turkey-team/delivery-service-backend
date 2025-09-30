package com.sparta.delivery.backend.reply.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.reply.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, UUID> {
}