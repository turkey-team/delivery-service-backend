package com.sparta.delivery.backend.order.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.order.dto.ReqUpdateOrderStatusDto;
import com.sparta.delivery.backend.order.entity.Order;
import com.sparta.delivery.backend.order.entity.OrderMenu;
import com.sparta.delivery.backend.order.enums.OrderStatus;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.payment.entity.PayMethod;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@DataJpaTest
@DisplayName("OrderRepository 테스트")
class OrderRepositoryTest {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderMenuRepository orderMenuRepository;

	@Autowired
	private TestEntityManager em;

	private User ownerUser;
	private User customerUser;
	private Owner owner;
	private Customer customer;
	private Store store;
	private StoreMenu menu;
	private Image image;
	private Order order;
	private Sido sido;
	private Sigungu sigungu;
	private Dong dong;

	@BeforeEach
	void setup() {
		// 유저 생성
		ownerUser = User.builder()
			.username("owner유저")
			.password("1234")
			.role(UserRoleEnum.OWNER)
			.build();
		em.persist(ownerUser);

		customerUser = User.builder()
			.username("customer유저")
			.password("5678")
			.role(UserRoleEnum.CUSTOMER)
			.build();
		em.persist(customerUser);

		owner = Owner.builder()
			.user(ownerUser)
			.nickname("테스트점주")
			.email("owner@test.com")
			.phoneNumber("010-0000-0000")
			.build();
		em.persist(owner);

		customer = Customer.builder()
			.user(customerUser)
			.nickname("테스트고객")
			.email("hong@test.com")
			.phoneNumber("010-1234-5678")
			.build();
		em.persist(customer);

		// 지역 엔티티
		sido = Sido.builder()
			.code("11")
			.name("서울특별시")
			.build();
		em.persist(sido);

		sigungu = Sigungu.builder()
			.code("680")
			.name("강남구")
			.sido(sido)
			.build();
		em.persist(sigungu);

		dong = Dong.builder()
			.code("101")
			.name("삼성동")
			.sigungu(sigungu)
			.build();
		em.persist(dong);

		// 이미지
		image = Image.builder()
			.imageUrl("https://test.com/image.jpg")
			.build();
		em.persist(image);

		// 가게 및 메뉴
		store = Store.builder()
			.owner(owner)
			.name("테스트가게")
			.addressDetails("서울시 강남구")
			.reviewRate(4.5)
			.minOrderPrice(10000)
			.deliveryFee(2000)
			.regionDong(dong)
			.status(StoreStatusEnum.OPEN)
			.phoneNumber("02-1111-2222")
			.build();
		em.persist(store);

		menu = StoreMenu.builder()
			.reqCreateStoreMenuDto(TestMenuFactory.create("치즈버거", 5500, StockStatus.ON_SALE))
			.store(store)
			.image(image)
			.build();
		menu.setSortOrder(1);
		em.persist(menu);

		// 주문
		order = Order.builder()
			.store(store)
			.customer(customer)
			.dongEntity(dong)
			.gu("강남구")
			.dong("삼성동")
			.addressDetails("삼성로 100")
			.orderStatus(OrderStatus.ORDERED)
			.payMethod(PayMethod.CARD)
			.build();
		em.persist(order);

		em.flush();
		em.clear();
	}

	@Nested
	@DisplayName("findByCustomerId 테스트")
	class FindByCustomerIdTests {

		@Test
		@DisplayName("성공 - 고객 ID로 주문 목록 조회")
		void success_findByCustomerId() {
			List<Order> result = orderRepository.findByCustomerId(customer.getId(), null).getContent();
			assertThat(result).hasSize(1);
			assertThat(result.get(0).getDong()).isEqualTo("삼성동");
		}

		@Test
		@DisplayName("성공 - 존재하지 않는 고객 ID 조회 시 빈 리스트 반환")
		void success_emptyWhenNotFound() {
			List<Order> result = orderRepository.findByCustomerId(UUID.randomUUID(), null).getContent();
			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("findByStoreOwnerId 테스트")
	class FindByOwnerIdTests {

		@Test
		@DisplayName("성공 - 점주 ID로 주문 목록 조회")
		void success_findByOwnerId() {
			List<Order> result = orderRepository.findByStoreOwnerId(owner.getId(), null).getContent();
			assertThat(result).hasSize(1);
			assertThat(result.get(0).getGu()).isEqualTo("강남구");
		}

		@Test
		@DisplayName("성공 - 잘못된 점주 ID 조회 시 빈 리스트 반환")
		void success_emptyWhenWrongOwner() {
			List<Order> result = orderRepository.findByStoreOwnerId(UUID.randomUUID(), null).getContent();
			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("주문 CRUD 테스트")
	class CrudTests {

		@Test
		@DisplayName("성공 - 주문 저장 및 조회")
		void success_saveAndFind() {
			Optional<Order> found = orderRepository.findById(order.getId());
			assertThat(found).isPresent();
			assertThat(found.get().getGu()).isEqualTo("강남구");
			assertThat(found.get().getOrderStatus()).isEqualTo(OrderStatus.ORDERED);
		}

		@Test
		@DisplayName("성공 - 주문 메뉴 연결 확인")
		void success_orderMenuRelation() {
			OrderMenu orderMenu = OrderMenu.builder()
				.order(order)
				.storeMenu(menu)
				.build();
			em.persist(orderMenu);

			List<OrderMenu> menus = orderMenuRepository.findAll();
			assertThat(menus).hasSize(1);
			assertThat(menus.get(0).getStoreMenu().getName()).isEqualTo("치즈버거");
		}
	}

	@Nested
	@DisplayName("주문 상태 변경 테스트")
	class UpdateStatusTests {

		@Test
		@DisplayName("성공 - 주문 ACCEPTED로 변경")
		void success_updateStatus() {
			ReqUpdateOrderStatusDto dto = new ReqUpdateOrderStatusDto();
			dto.setOrderStatus(OrderStatus.ACCEPTED);

			order.updateOrderStatus(customerUser, dto);

			assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
			assertThat(order.getCancelledAt()).isNull();
		}

		@Test
		@DisplayName("성공 - 주문 CANCELLED 시 취소 관련 필드 세팅")
		void success_cancelOrder() {
			ReqUpdateOrderStatusDto dto = new ReqUpdateOrderStatusDto();
			dto.setOrderStatus(OrderStatus.CANCELLED);
			dto.setCancelledReason("재고 없음");

			order.updateOrderStatus(customerUser, dto);

			assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
			assertThat(order.getCancelledAt()).isNotNull();
			assertThat(order.getCancelledBy()).isEqualTo(customerUser.getId());
			assertThat(order.getCancelledReason()).isEqualTo("재고 없음");
		}

		@Test
		@DisplayName("실패 - 이미 처리된 주문은 예외 발생")
		void failure_alreadyProcessed() {
			// given
			ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.ACCEPTED);

			ReqUpdateOrderStatusDto dto = new ReqUpdateOrderStatusDto();
			dto.setOrderStatus(OrderStatus.CANCELLED);

			// when & then
			assertThatThrownBy(() -> order.updateOrderStatus(customerUser, dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("이미 처리된 주문입니다.");
		}
	}

	// 내부 테스트용 DTO 팩토리
	private static class TestMenuFactory {
		static com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto create(String name, int price, StockStatus status) {
			com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto dto =
				new com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto();
			dto.setName(name);
			dto.setPrice(price);
			dto.setDescription("테스트용 메뉴: " + name);
			dto.setPrepTime("10분");
			dto.setStockStatus(status);
			dto.setIsHidden(false);
			dto.setImageUrl("https://test.com/" + name + ".jpg");
			return dto;
		}
	}
}