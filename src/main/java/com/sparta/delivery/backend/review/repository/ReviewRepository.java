package com.sparta.delivery.backend.review.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.delivery.backend.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepoistoryCustom {

	// 각 Void 부분 대신 Store쪽에서 평균 rating과 같이 보여줄 dto 만들어서 넣어주면 됩니다.
	// 평균평점이 함께 들어가있는 Store List로 조회
	@Query("select s.id as storeId, s.name as storeName, "
		+ "avg(r.rate) as avgRate, count(r.id) as reviewCnt from Store s left join Review r on s.id = r.store.id "
		+ "where r.deletedAt = null "
		+ "group by s.id")
	List<Void> findStoresWithAverageRating();

	// 각 Void 부분 대신 Store쪽에서 평균 rating과 같이 보여줄 dto 만들어서 넣어주면 됩니다.
	// 평균평점이 함께 들어가있는 Store 단건 조회
	@Query("select s.id as storeId, s.name as storeName, "
		+ "avg(r.rate) as avgRate, count(r.id) as reviewCnt from Store s left join Review r on s.id = r.store.id "
		+ "where s.id = :storeId and r.deletedAt = null "
		+ "group by s.id")
	Optional<Void> findStoreWithAverageRating(@Param("storeId") UUID storeId);

	// store service에서 작성 => N+1 문제 해결
	/*for (Store store : stores) {
		Double avg = reviewRepository.findStoreWithAverageRating(store.getId());
	}*/

}
