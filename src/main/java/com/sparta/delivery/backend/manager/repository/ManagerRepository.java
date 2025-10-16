package com.sparta.delivery.backend.manager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.manager.entity.Manager;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, UUID> {
	Optional<Manager> findByUserId(Long userId);

	@Query("""
		SELECT m FROM Manager m
		JOIN FETCH m.user
		WHERE m.deletedAt IS NULL
		""")
	List<Manager> findAllByDeletedAtIsNull();

	@Query("""
		SELECT m FROM Manager m 
		JOIN FETCH m.user
		WHERE m.user.publicId = :publicId
		AND m.deletedAt IS NULL
		""")
	Optional<Manager> findByUserPublicIdAndDeletedAtIsNull(@Param("publicId") UUID publicId);

}