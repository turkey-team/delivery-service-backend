package com.sparta.delivery.backend.manager.repository;

import com.sparta.delivery.backend.manager.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, UUID> {
}
