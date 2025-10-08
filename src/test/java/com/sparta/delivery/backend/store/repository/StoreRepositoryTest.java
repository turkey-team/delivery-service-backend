package com.sparta.delivery.backend.store.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.category.repository.CategoryRepository;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.manager.entity.Manager;
import com.sparta.delivery.backend.manager.repository.ManagerRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.store.dto.ResGetListStoreDto;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreCategory;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
import com.sparta.delivery.backend.user.repository.UserRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@Import(StoreRepositoryImpl.class) // QueryDSL 구현체를 빈으로 등록
@ActiveProfiles("test")
@Transactional
public class StoreRepositoryTest {

	@Autowired
	private StoreRepositoryImpl storeRepositoryImpl;

	@Autowired
	private EntityManager em;

	private User testUser;
	private Manager manager;

	private User ownerUser;
	private Owner owner;

	private Category pizzaCategory;
	private Category chickenCategory;
	private Store storeA;
	private Store storeB;
	private Store storeC;


	@BeforeEach
	void setUp() throws InterruptedException {
		// Given

		// 0. manager
		testUser = User.builder()
			.username("testUser")
			.password("password123")
			.role(UserRoleEnum.MANAGER)
			.build();
		em.persist(testUser);

		manager = Manager.builder()
			.user(testUser)
			.name("매니저")
			.phoneNumber("01012345678")
			.email("test@test.net")
			.build();

		em.persist(manager);

		ownerUser = User.builder()
			.username("ownerUser")
			.password("password456")
			.role(UserRoleEnum.OWNER)
			.build();
		em.persist(ownerUser);

		owner = Owner.builder()
			.nickname("사장님")
			.businessNumber("1234567890")
			.user(ownerUser)
			.email("owner@test.net")
			.phoneNumber("01098745632")
			.build();

		em.persist(owner);

		// 1. 카테고리
		pizzaCategory = Category.builder().name("피자").build();
		chickenCategory = Category.builder().name("치킨").build();
		Category deletedCategory = Category.builder().name("삭제됨").build();
		deletedCategory.softDelete(1l);

		em.persist(pizzaCategory);
		em.persist(chickenCategory);
		em.persist(deletedCategory);

		// 2. 가게
		storeA = Store.builder().name("도미노피자").reviewRate(4.8).deliveryFee(3000).minOrderPrice(15000).status(StoreStatusEnum.CLOSED).owner(owner).build();
		em.persist(storeA);

		Thread.sleep(100);

		storeB = Store.builder().name("굽네치킨").reviewRate(4.5).deliveryFee(2000).minOrderPrice(10000).status(StoreStatusEnum.CLOSED).owner(owner).build();
		em.persist(storeB);

		Thread.sleep(100);

		storeC = Store.builder().name("다른피자").reviewRate(4.9).deliveryFee(4000).minOrderPrice(20000).status(StoreStatusEnum.OPEN).owner(owner).build();
		em.persist(storeC);

		Thread.sleep(100);


		// 3. 가게-카테고리 연관관계 설정
		em.persist(StoreCategory.builder().store(storeA).category(pizzaCategory).build());
		em.persist(StoreCategory.builder().store(storeB).category(chickenCategory).build());
		em.persist(StoreCategory.builder().store(storeC).category(pizzaCategory).build());
		em.persist(StoreCategory.builder().store(storeA).category(chickenCategory).build());

		em.flush(); // DB에 반영
	}

	// 성공 케이스

	@Test
	@DisplayName("전체 조회 시 정상적으로 모든 가게를_페이지네이션하여 반환한다")
	void getStores_NoFilters_ReturnsAllStoresPaginated() {
		// Given
		Pageable pageable = PageRequest.of(0, 10); // 첫 페이지, 2개씩

		// When
		Page<ResGetListStoreDto> result = storeRepositoryImpl.getStores(pageable, "newest", null, null);

		// Then
		assertAll(
			() -> assertThat(result.getTotalElements()).isEqualTo(3), // 총 3개
			() -> assertThat(result.getContent()).hasSize(3),
			() -> assertThat(result.getContent().get(0).getName()).isEqualTo("다른피자"), // 최신순
			() -> assertThat(result.getContent().get(1).getName()).isEqualTo("굽네치킨"), // 다음 최신순
			() -> assertThat(result.getContent().get(2).getName()).isEqualTo("도미노피자")
		);
	}

	@Test
	@DisplayName("카테고리 ID로 필터링 시 해당 카테고리의 가게만 반환한다")
	void getStores_FilterByCategory_ReturnsFilteredStores() {
		// Given
		Pageable pageable = PageRequest.of(0, 10);

		// When: 피자 카테고리로 조회 (storeA, storeC)
		Page<ResGetListStoreDto> result = storeRepositoryImpl.getStores(pageable, "newest", null, pizzaCategory.getId());

		// Then
		assertThat(result.getTotalElements()).isEqualTo(2);
		List<String> storeNames = result.getContent().stream().map(ResGetListStoreDto::getName).toList();
		assertThat(storeNames).containsExactlyInAnyOrder("도미노피자", "다른피자");
	}

	@Test
	@DisplayName("검색 결과가 없을 경우")
	void getStores_NoMatchingKeyword_ReturnsEmptyPage() {
		// Given
		Pageable pageable = PageRequest.of(0, 10);
		String keyword = "존재하지않는가게";

		// When
		Page<ResGetListStoreDto> result = storeRepositoryImpl.getStores(pageable, "newest", keyword, null);

		// Then
		assertThat(result.getTotalElements()).isEqualTo(0);
		assertThat(result.getContent()).isEmpty();
	}
}
