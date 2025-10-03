package com.sparta.delivery.backend.manager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.manager.entity.Manager;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, UUID> {
	Optional<Manager> findByUserId(Long userId);
}
