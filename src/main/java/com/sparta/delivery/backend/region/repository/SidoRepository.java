package com.sparta.delivery.backend.region.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.delivery.backend.region.entity.Sido;

public interface SidoRepository extends JpaRepository<Sido, UUID> {

	@Query("""
		SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
		FROM Sido s
		WHERE s.name IN :names AND s.deletedAt IS NULL
		""")
	boolean existsByNameInCustom(@Param("names") List<String> names);

	@Query("""
		SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
		FROM Sido s
		WHERE s.code In :codes AND s.deletedAt IS NULL
		""")
	boolean existsByCodeInCustom(@Param("codes") List<String> codes);

	@Query("""
		SELECT s
		FROM Sido s
		WHERE s.deletedAt IS NULL
		""")
	List<Sido> findAllCustom();

	@Query("""
		SELECT s
		FROM Sido s
		WHERE s.id = :sidoId AND s.deletedAt IS NULL
		""")
	Optional<Sido> findByIdCustom(@Param("sidoId") UUID sidoId);

	@Query("""
		SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
		FROM Sido s
		WHERE s.name = :name AND s.id != :sidoId AND s.deletedAt IS NULL
		""")
	boolean existsByNameAndIdNotCustom(@Param("name") String name, @Param("sidoId") UUID sidoId);

	@Query("""
		SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
		FROM Sido s
		WHERE s.code = :code AND s.id != :sidoId AND s.deletedAt IS NULL
		""")
	boolean existsByCodeAndIdNotCustom(@Param("code") String code, @Param("sidoId") UUID sidoId);

}
