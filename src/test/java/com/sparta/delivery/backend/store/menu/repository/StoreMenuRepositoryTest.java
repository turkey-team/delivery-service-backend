package com.sparta.delivery.backend.store.menu.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@DataJpaTest
@DisplayName("StoreMenuRepository 테스트")
class StoreMenuRepositoryTest {

	@Autowired
	private StoreMenuRepository storeMenuRepository;

	@Autowired
	private TestEntityManager em;

	private User user;
	private Owner owner;
	private Store store;
	private Image image;
	private StoreMenu menu1;
	private StoreMenu menu2;

	@BeforeEach
	void setup() {
		user = User.builder()
			.username("storeOwner")
			.password("password123")
			.role(UserRoleEnum.OWNER)
			.build();
		em.persist(user);

		owner = Owner.builder()
			.user(user)
			.nickname("테스트 사장님")
			.email("owner@test.com")
			.phoneNumber("01012345678")
			.build();
		em.persist(owner);

		store = Store.builder()
			.owner(owner)
			.name("테스트 가게")
			//.addressDetails("서울시 마포구의 문앞에 있어요")
			.reviewRate(4.5)
			.minOrderPrice(10000)
			.deliveryFee(2000)
			//.regionDong(null)
			.status(StoreStatusEnum.OPEN)
			.phoneNumber("0212345678")
			.build();
		em.persist(store);

		image = Image.builder()
			.imageUrl("http://test.com/image.jpg")
			.build();
		em.persist(image);

		// 메뉴 1 (정상)
		menu1 = StoreMenu.builder()
			.reqCreateStoreMenuDto(TestMenuFactory.create("치즈버거", 5000, StockStatus.ON_SALE))
			.store(store)
			.image(image)
			.build();
		menu1.setSortOrder(1);
		em.persist(menu1);

		// 메뉴 2 (삭제된 메뉴)
		menu2 = StoreMenu.builder()
			.reqCreateStoreMenuDto(TestMenuFactory.create("불고기버거", 6000, StockStatus.SOLD_OUT))
			.store(store)
			.image(image)
			.build();
		menu2.setSortOrder(2);
		menu2.softDelete(null);
		em.persist(menu2);

		em.flush();
		em.clear();
	}

	@Nested
	@DisplayName("findAllByStoreIdAndDeletedAtIsNull 테스트")
	class FindAllActiveMenus {

		@Test
		@DisplayName("성공 - 삭제되지 않은 메뉴만 조회됨")
		void success_findActiveMenus() {
			List<StoreMenu> menus = storeMenuRepository.findAllByStoreIdAndDeletedAtIsNull(store.getId(), null)
				.getContent();
			assertThat(menus)
				.extracting("name")
				.containsExactly("치즈버거");
		}

		@Test
		@DisplayName("실패 - softDelete된 메뉴는 제외됨")
		void failure_excludeDeletedMenus() {
			List<StoreMenu> menus = storeMenuRepository.findAllByStoreIdAndDeletedAtIsNull(store.getId(), null)
				.getContent();
			assertThat(menus)
				.noneMatch(menu -> menu.getDeletedAt() != null);
		}
	}

	@Nested
	@DisplayName("findByStoreIdAndName 테스트")
	class FindByNameTest {

		@Test
		@DisplayName("성공 - 이름으로 메뉴 조회 가능")
		void success_findByName() {
			Optional<StoreMenu> found = storeMenuRepository.findByStoreIdAndName(store.getId(), "치즈버거");
			assertThat(found).isPresent();
			assertThat(found.get().getPrice()).isEqualTo(5000);
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 이름 조회 시 empty 반환")
		void failure_nameNotFound() {
			Optional<StoreMenu> found = storeMenuRepository.findByStoreIdAndName(store.getId(), "새우버거");
			assertThat(found).isEmpty();
		}
	}

	@Nested
	@DisplayName("findByStoreIdAndIdAndDeletedAtIsNull 테스트")
	class FindByIdTest {

		@Test
		@DisplayName("성공 - 정상 메뉴 조회")
		void success_findById() {
			Optional<StoreMenu> found = storeMenuRepository
				.findByStoreIdAndIdAndDeletedAtIsNull(store.getId(), menu1.getId(), null);
			assertThat(found).isPresent();
			assertThat(found.get().getName()).isEqualTo("치즈버거");
		}

		@Test
		@DisplayName("실패 - softDelete된 메뉴는 조회되지 않음")
		void failure_deletedMenuExcluded() {
			Optional<StoreMenu> found = storeMenuRepository
				.findByStoreIdAndIdAndDeletedAtIsNull(store.getId(), menu2.getId(), null);
			assertThat(found).isEmpty();
		}
	}

	@Nested
	@DisplayName("findMaxSortOrderByStore 테스트")
	class FindMaxSortOrderTest {

		@Test
		@DisplayName("성공 - 가장 큰 sortOrder 반환")
		void success_maxSortOrder() {
			Integer max = storeMenuRepository.findMaxSortOrderByStore(store.getId());
			assertThat(max).isEqualTo(1); // 삭제된 메뉴의 sortOrder 2 는 음수로 바뀌어버리니까
		}

		@Test
		@DisplayName("성공 - 메뉴가 없으면 null 반환")
		void success_noMenusReturnsNull() {
			Store emptyStore = Store.builder()
				.owner(owner)
				.name("빈가게")
				//.addressDetails("서울시 중구 쯔음에 있어요")
				.reviewRate(0)
				.minOrderPrice(0)
				.deliveryFee(0)
				//.regionDong(null)
				.status(StoreStatusEnum.OPEN)
				.phoneNumber("01012345678")
				.build();
			em.persist(emptyStore);

			Integer max = storeMenuRepository.findMaxSortOrderByStore(emptyStore.getId());
			assertThat(max).isNull();
		}
	}

	// 테스트용 메뉴 DTO 팩토리 (내부 static)
	private static class TestMenuFactory {
		static com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto create(String name, int price,
			StockStatus status) {
			com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto dto =
				new com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto();
			dto.setName(name);
			dto.setPrice(price);
			dto.setDescription("테스트용 메뉴: " + name);
			dto.setPrepTime("10분");
			dto.setStockStatus(status);
			dto.setIsHidden(false);
			dto.setImageUrl("http://test.com/" + name + ".jpg");
			return dto;
		}
	}
}