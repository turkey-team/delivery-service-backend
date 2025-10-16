package com.sparta.delivery.backend.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.address.repository.AddressRepository;
import com.sparta.delivery.backend.cart.entity.Cart;
import com.sparta.delivery.backend.cart.repository.CartRepository;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.entity.CustomerAddress;
import com.sparta.delivery.backend.customer.repository.CustomerAddressRepository;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.global.common.dto.PageResponse;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.order.dto.ReqCreateOrderDto;
import com.sparta.delivery.backend.order.dto.ReqUpdateOrderStatusDto;
import com.sparta.delivery.backend.order.entity.Order;
import com.sparta.delivery.backend.order.entity.OrderMenu;
import com.sparta.delivery.backend.order.enums.OrderStatus;
import com.sparta.delivery.backend.order.repository.OrderMenuRepository;
import com.sparta.delivery.backend.order.repository.OrderRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.payment.entity.PayMethod;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 테스트")
class OrderServiceTest {

	@InjectMocks
	private OrderService orderService;

	@Mock
	private CartRepository cartRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private OwnerRepository ownerRepository;
	@Mock
	private CustomerAddressRepository customerAddressRepository;
	@Mock
	private OrderMenuRepository orderMenuRepository;

	private User customerUser;
	private User ownerUser;
	private Customer customer;
	private Owner owner;
	private Store store;
	private StoreMenu storeMenu;
	private Order order;
	private Address address;
	private CustomerAddress customerAddress;
	private Cart cart;
	private Dong dong;
	private Sigungu sigungu;
	private Sido sido;
	private ReqCreateStoreMenuDto reqCreateStoreMenuDto;
	private Image image;

	@BeforeEach
	void setup() {
		// 테스트용 User(Customer) 생성
		customerUser = User.builder()
			.username("customerUser")
			.password("pass")
			.role(UserRoleEnum.CUSTOMER)
			.build();
		ReflectionTestUtils.setField(customerUser, "id", 1L);
		ReflectionTestUtils.setField(customerUser, "publicId", UUID.randomUUID());

		customer = Customer.builder()
			.user(customerUser)
			.nickname("테스트고객")
			.build();
		ReflectionTestUtils.setField(customer, "id", UUID.randomUUID());

		// 테스트용 User(Owner) 생성
		ownerUser = User.builder()
			.username("ownerUser")
			.password("pass")
			.role(UserRoleEnum.OWNER)
			.build();
		ReflectionTestUtils.setField(ownerUser, "id", 2L);
		ReflectionTestUtils.setField(ownerUser, "publicId", UUID.randomUUID());

		owner = Owner.builder()
			.user(ownerUser)
			.nickname("테스트점주")
			.build();
		ReflectionTestUtils.setField(owner, "id", UUID.randomUUID());

		// 테스트 주소
		sido = Sido.builder()
			.code("11")
			.name("서울특별시")
			.build();

		sigungu = Sigungu.builder()
			.code("680")
			.name("강남구")
			.sido(sido)
			.build();

		dong = Dong.builder()
			.sigungu(sigungu)
			.name("삼성동")
			.code("101")
			.build();
		
		address = Address.builder()
			.dong(dong)
			.build();
		ReflectionTestUtils.setField(address, "id", UUID.randomUUID());

		customerAddress = CustomerAddress.builder()
			.isDefault(true)
			.address(address)
			.nickname("우리집")
			.customer(customer)
			.build();
		ReflectionTestUtils.setField(customerAddress, "id", UUID.randomUUID());

		// 테스트 가게
		store = Store.builder()
			.owner(owner)
			.name("테스트가게")
			.address(Address.builder().dong(dong).fullAddress("강남").build())
			.deliveryFee(2000)
			.minOrderPrice(10000)
			.build();
		ReflectionTestUtils.setField(store, "id", UUID.randomUUID());
		
		// 테스트 주문
		order = Order.builder()
			.customer(customer)
			.store(store)
			.orderStatus(OrderStatus.ORDERED)
			.payMethod(PayMethod.CARD)
			.build();
		ReflectionTestUtils.setField(order, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(order, "dongEntity", dong);
		ReflectionTestUtils.setField(order, "gu", "강남구");
		ReflectionTestUtils.setField(order, "dong", "삼성동");
		ReflectionTestUtils.setField(order, "addressDetails", "테스트주소 123입니다아아");
		ReflectionTestUtils.setField(order, "createdAt", Instant.now());

		// 테스트 이미지
		image = Image.builder()
			.imageUrl("https://example.com/hamburger.jpg")
			.build();

		// 테스트 메뉴
		reqCreateStoreMenuDto = new ReqCreateStoreMenuDto();
		reqCreateStoreMenuDto.setName("치즈버거");
		reqCreateStoreMenuDto.setImageUrl(image.getImageUrl());
		reqCreateStoreMenuDto.setPrice(4000);
		reqCreateStoreMenuDto.setDescription("치즈, 소고기, 피클, 마요네즈가 들어있습니다.");
		reqCreateStoreMenuDto.setPrepTime("15분");
		reqCreateStoreMenuDto.setStockStatus(StockStatus.ON_SALE);
		reqCreateStoreMenuDto.setIsHidden(false);

		storeMenu = StoreMenu.builder()
			.reqCreateStoreMenuDto(reqCreateStoreMenuDto)
			.store(store)
			.image(image)
			.build();
		ReflectionTestUtils.setField(storeMenu, "id", UUID.randomUUID()); // Test UUID 주입
		storeMenu.setSortOrder(1);

		// 테스트 주문 리스트
		List<OrderMenu> orderMenus = List.of(
			OrderMenu.builder()
				.order(order)
				.storeMenu(storeMenu)
				.build()
		);
		ReflectionTestUtils.setField(order, "orderMenus", orderMenus);

		// 테스트 장바구니
		cart = Cart.builder()
			.customer(customer)
			.menu(storeMenu)
			.build();
		ReflectionTestUtils.setField(cart, "id", UUID.randomUUID());
	}

	@Nested
	@DisplayName("주문 생성 테스트")
	class CreateOrderTest {

		@Test
		@DisplayName("성공 - 기본 주소와 장바구니 기반으로 주문 생성")
		void create_orderWithDefaultAddressAndCart() {
			/* given */
			ReqCreateOrderDto req = new ReqCreateOrderDto();
			req.setRequestMessage("문앞에 두세요");

			when(customerRepository.findByUserId(any())).thenReturn(Optional.of(customer));
			when(customerAddressRepository.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(any()))
				.thenReturn(Optional.of(customerAddress));
			when(cartRepository.findAllByCustomerIdAndDeletedAtIsNull(any()))
				.thenReturn(List.of(cart));

			when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
				Order saved = inv.getArgument(0);
				ReflectionTestUtils.setField(saved, "id", UUID.randomUUID());
				return saved;
			});

			when(orderMenuRepository.save(any(OrderMenu.class)))
				.thenAnswer(inv -> inv.getArgument(0));

			/* when */
			UUID orderId = orderService.createOrder(customerUser, req);

			/* then */
			assertNotNull(orderId);
			verify(orderRepository, times(1)).save(any(Order.class));
			verify(orderMenuRepository, atLeastOnce()).save(any(OrderMenu.class));
		}

		@Test
		@DisplayName("실패 - 기본 주소 없음")
		void failure_noAddress() {
			/* given */
			lenient().when(customerRepository.findByUserId(any())).thenReturn(Optional.of(customer));
			lenient().when(customerAddressRepository.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(any()))
				.thenReturn(Optional.empty());

			/* when & then */
			assertThrows(IllegalStateException.class,
				() -> orderService.createOrder(customerUser, new ReqCreateOrderDto()));
		}

		@Test
		@DisplayName("실패 - 장바구니 주인이 아닌 사용자가 주문 생성")
		void failure_notCartOwner() {
			/* given */
			// 다른 Customer 유저 생성
			User anotherUser = User.builder()
				.username("otherCustomer")
				.password("pass")
				.role(UserRoleEnum.CUSTOMER)
				.build();
			ReflectionTestUtils.setField(anotherUser, "id", 99L);
			ReflectionTestUtils.setField(anotherUser, "publicId", UUID.randomUUID());

			Customer anotherCustomer = Customer.builder()
				.user(anotherUser)
				.nickname("다른고객")
				.build();
			ReflectionTestUtils.setField(anotherCustomer, "id", UUID.randomUUID());

			// Cart 가 다른 Customer 것이라면
			Cart foreignCart = Cart.builder()
				.customer(anotherCustomer)
				.menu(storeMenu)
				.build();

			when(customerRepository.findByUserId(any())).thenReturn(Optional.of(customer));
			when(customerAddressRepository.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(any()))
				.thenReturn(Optional.of(customerAddress));
			when(cartRepository.findAllByCustomerIdAndDeletedAtIsNull(any()))
				.thenReturn(List.of(foreignCart));

			/* when & then */
			SecurityException exception = assertThrows(
				SecurityException.class,
				() -> orderService.createOrder(customerUser, new ReqCreateOrderDto())
			);

			assertEquals("본인 장바구니가 아닙니다.", exception.getMessage());
		}
	}

	@Nested
	@DisplayName("주문 목록 조회 테스트")
	class GetOrderListTest {

		@Test
		@DisplayName("성공 - Customer의 주문 조회")
		void getOrders_customer_success() {
			/* given */
			Page<Order> mockPage = new PageImpl<>(List.of(order));

			when(customerRepository.findByUserId(customerUser.getId())).thenReturn(Optional.of(customer));
			when(orderRepository.findByCustomerId(any(), any(Pageable.class))).thenReturn(mockPage);

			/* when */
			PageResponse<?> result = orderService.getOrdersByUser(customerUser, 0, 10);

			/* then */
			assertEquals(1, result.getTotalElements());
		}

		@Test
		@DisplayName("성공 - Owner의 주문 조회")
		void getOrders_owner_success() {
			/* given */
			Page<Order> mockPage = new PageImpl<>(List.of(order));

			when(ownerRepository.findByUserId(ownerUser.getId())).thenReturn(Optional.of(owner));
			when(orderRepository.findByStoreOwnerId(any(), any(Pageable.class))).thenReturn(mockPage);

			/* when */
			PageResponse<?> result = orderService.getOrdersByUser(ownerUser, 0, 10);

			/* then */
			assertEquals(1, result.getContent().size());
		}
	}

	@Nested
	@DisplayName("주문 상태 변경 테스트")
	class UpdateOrderStatusTest {

		@Test
		@DisplayName("성공 - 해당 가게의 Owner가 주문 상태 변경")
		void updateStatus_success() {
			/* given */
			when(orderRepository.findById(any())).thenReturn(Optional.of(order));
			when(orderRepository.save(any())).thenReturn(order);

			ReqUpdateOrderStatusDto req = new ReqUpdateOrderStatusDto();
			req.setOrderStatus(OrderStatus.ACCEPTED);

			/* when */
			orderService.updateOrderStatus(ownerUser, order.getId(), req);

			/* then */
			verify(orderRepository).save(order);
			assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
		}

		@Test
		@DisplayName("성공 - Customer가 주문 생성 5분 이내에 취소")
		void cancelByCustomer_within5Minutes_success() {
			/* given */
			when(orderRepository.findById(any())).thenReturn(Optional.of(order));
			when(orderRepository.save(any())).thenReturn(order);

			// 현재 주문 중 상태이고
			ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.ORDERED);

			// 만약 주문 후 4분이 지났다면
			ReflectionTestUtils.setField(order, "createdAt", Instant.now().minus(4, ChronoUnit.MINUTES));

			// 취소 요청 가능
			ReqUpdateOrderStatusDto req = new ReqUpdateOrderStatusDto();
			req.setOrderStatus(OrderStatus.CANCELLED);

			/* when */
			orderService.updateOrderStatus(customerUser, order.getId(), req);

			/* then */
			verify(orderRepository).save(order);
			assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
		}

		@Test
		@DisplayName("실패 - Customer가 주문 생성 5분 초과 후 취소 시도")
		void cancelByCustomer_after5Minutes_fail() {
			/* given */
			when(orderRepository.findById(any())).thenReturn(Optional.of(order));

			// 현재 주문 중 상태이지만
			ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.ORDERED);

			// 만약 주문 후 5분이 넘었다면 (10분)
			ReflectionTestUtils.setField(order, "createdAt", Instant.now().minus(10, ChronoUnit.MINUTES));

			// 취소를 보내도 불가능
			ReqUpdateOrderStatusDto req = new ReqUpdateOrderStatusDto();
			req.setOrderStatus(OrderStatus.CANCELLED);

			/* when & then */
			IllegalStateException ex = assertThrows(
				IllegalStateException.class,
				() -> orderService.updateOrderStatus(customerUser, order.getId(), req)
			);

			assertEquals("주문 5분 이내에만 주문을 취소할 수 있습니다.", ex.getMessage());
			verify(orderRepository, never()).save(any());
		}

		@Test
		@DisplayName("실패 - Customer가 주문 중 상태가 아닌 경우에 취소 시도")
		void cancelByCustomer_notOrdered_fail() {
			/* given */
			when(orderRepository.findById(any())).thenReturn(Optional.of(order));

			// 현재 주문 중 상태가 아니라면
			ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.ACCEPTED);

			// 만약 주문 후 5분이 안지났더라도
			ReflectionTestUtils.setField(order, "createdAt", Instant.now().minus(2, ChronoUnit.MINUTES));

			// 취소 불가능
			ReqUpdateOrderStatusDto req = new ReqUpdateOrderStatusDto();
			req.setOrderStatus(OrderStatus.CANCELLED);

			/* when & then */
			IllegalStateException ex = assertThrows(
				IllegalStateException.class,
				() -> orderService.updateOrderStatus(customerUser, order.getId(), req)
			);

			assertEquals("주문중 상태에만 주문을 취소할 수 있습니다.", ex.getMessage());
			verify(orderRepository, never()).save(any());
		}

		@Test
		@DisplayName("실패 - Customer가 주문 수락 시도")
		void updateStatus_noPermission() {
			/* given */
			when(orderRepository.findById(any())).thenReturn(Optional.of(order));

			ReqUpdateOrderStatusDto req = new ReqUpdateOrderStatusDto();
			req.setOrderStatus(OrderStatus.ACCEPTED);

			/* when & then */
			assertThrows(IllegalStateException.class,
				() -> orderService.updateOrderStatus(customerUser, order.getId(), req));
		}

		@Test
		@DisplayName("실패 - 주문이 존재하지 않음")
		void updateStatus_orderNotExists() {
			/* given */
			UUID notExistsOrderId = UUID.randomUUID();
			when(orderRepository.findById(notExistsOrderId)).thenReturn(Optional.empty());

			ReqUpdateOrderStatusDto req = new ReqUpdateOrderStatusDto();
			req.setOrderStatus(OrderStatus.ACCEPTED);

			/* when & then */
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> orderService.updateOrderStatus(ownerUser, notExistsOrderId, req)
			);

			assertEquals("주문이 존재하지 않습니다.", exception.getMessage());
			verify(orderRepository, times(1)).findById(notExistsOrderId);
		}
	}

	@Nested
	@DisplayName("주문 삭제 테스트")
	class DeleteOrderTest {

		@Test
		@DisplayName("성공 - Owner가 본인 주문 내역 삭제")
		void delete_success() {
			/* given */
			when(orderRepository.findById(any())).thenReturn(Optional.of(order));
			ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.CANCELLED);

			/* when */
			orderService.deleteOrder(customerUser, order.getId());

			/* then */
			assertNotNull(order.getDeletedAt());
			verify(orderRepository).save(order);
		}

		@Test
		@DisplayName("실패 - 진행 중 주문 삭제 불가")
		void failure_inProgress() {
			/* given */
			when(orderRepository.findById(any())).thenReturn(Optional.of(order));
			ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.ORDERED);

			/* when & then */
			assertThrows(IllegalStateException.class,
				() -> orderService.deleteOrder(customerUser, order.getId()));
		}
	}
}
