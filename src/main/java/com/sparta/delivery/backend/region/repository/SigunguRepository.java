package com.sparta.delivery.backend.region.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;

public interface SigunguRepository extends JpaRepository<Sigungu, UUID> {

	@Query("""
		SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
		FROM Sigungu s
		WHERE s.name IN :names AND s.sido = :sido AND s.deletedAt IS NULL
		""")
	boolean existsByNameInAndSidoCustom(@Param("names") List<String> names, @Param("sido") Sido sido);

	@Query("""
		SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
		FROM Sigungu s
		WHERE s.code IN :codes AND s.deletedAt IS NULL
		""")
	boolean existsByCodeInCustom(@Param("codes") List<String> codes);

	@Query("""
		SELECT s
		FROM Sigungu s
		WHERE s.sido = :sido AND s.deletedAt IS NULL
		""")
	List<Sigungu> findAllBySidoCustom(@Param("sido") Sido sido);

	@Query("""
		SELECT s
		FROM Sigungu s
		WHERE s.id = :sigunguId AND s.sido = :sido AND s.deletedAt IS NULL
		""")
	Optional<Sigungu> findByIdAndSidoCustom(@Param("sigunguId") UUID sigunguId, @Param("sido") Sido sido);

	@Query("""
		SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
		FROM Sigungu s
		WHERE s.name = :name AND s.sido = :sido AND s.id != :sigunguId AND s.deletedAt IS NULL
		""")
	boolean existsByNameAndSidoAndIdNotCustom(@Param("name") String name, @Param("sido") Sido sido,
		@Param("sigunguId") UUID sigunguId);

	@Query("""
		SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
		FROM Sigungu s
		WHERE s.code = :code AND s.id != :sigunguId AND s.deletedAt IS NULL
		""")
	boolean existsByCodeAndIdNotCustom(@Param("code") String code, @Param("sigunguId") UUID sigunguId);

	@Query("""
		SELECT s
		FROM Sigungu s
		WHERE s.id = :sigunguId AND s.deletedAt IS NULL
		""")
	Optional<Sigungu> findByIdCustom(@Param("sigunguId") UUID sigunguId);

}
