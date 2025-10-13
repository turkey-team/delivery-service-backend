package com.sparta.delivery.backend.order.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.address.repository.AddressRepository;
import com.sparta.delivery.backend.cart.entity.Cart;
import com.sparta.delivery.backend.cart.repository.CartRepository;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.order.dto.ReqCreateOrderDto;
import com.sparta.delivery.backend.order.dto.ReqUpdateOrderStatusDto;
import com.sparta.delivery.backend.order.dto.ResCheckOutOrderDto;
import com.sparta.delivery.backend.order.dto.ResGetListOrderDto;
import com.sparta.delivery.backend.order.dto.ResGetOrderDto;
import com.sparta.delivery.backend.order.entity.Order;
import com.sparta.delivery.backend.order.entity.OrderMenu;
import com.sparta.delivery.backend.order.enums.OrderStatus;
import com.sparta.delivery.backend.order.repository.OrderMenuRepository;
import com.sparta.delivery.backend.order.repository.OrderRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final CartRepository cartRepository;
	private final OrderRepository orderRepository;
	private final CustomerRepository customerRepository;
	private final OwnerRepository ownerRepository;
	private final AddressRepository addressRepository;
	private final OrderMenuRepository orderMenuRepository;

	/** 생성 **/
	@Transactional
	public UUID createOrder(User user, ReqCreateOrderDto reqCreateOrderDto) {

		// Checkout 정보 계산 (Cart 조회, 단일 매장 검증, 가격 계산)
		ResCheckOutOrderDto checkout = calculateCheckout(user);

		// 주문할 가게는 모두 동일
		Store store = getStoreFromCart(user);

		// Address 엔티티에서 Dong 객체 가져오기
		Address defaultAddressEntity = addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(user.getId())
			.orElseThrow(() -> new IllegalStateException("기본 주소 정보가 없습니다."));

		// 주문할 고객 customer 조회
		Customer customer = getCustomerOrThrow(user);

		// Order 엔티티 생성 및 저장
		Order order = Order.builder()
			.customer(customer)
			.store(store)
			.dongEntity(defaultAddressEntity.getDong())
			.gu(defaultAddressEntity.getDong().getSigungu().getName())
			.dong(defaultAddressEntity.getDong().getName())
			.addressDetails(checkout.getAddressDetail())
			.orderStatus(OrderStatus.ORDERED)
			.requestMessage(reqCreateOrderDto.getRequestMessage())
			.payMethod(reqCreateOrderDto.getPayMethod())
			.build();
		orderRepository.save(order);

		// Cart → OrderMenu 변환 후, Cart 비우기
		// Cart 조회
		List<Cart> carts = cartRepository.findAllByCustomerIdAndDeletedAtIsNull(customer.getId());
		if (carts.isEmpty()) throw new IllegalStateException("장바구니가 비어 있습니다.");

		// 메뉴별로 Grouping + row 개수 count
		Map<StoreMenu, Long> menuCountMap = carts.stream()
				.collect(Collectors.groupingBy(Cart::getMenu, Collectors.counting()));

		// OrderMenu 생성
		menuCountMap.forEach((menu, count) -> {
			orderMenuRepository.save(OrderMenu.builder()
					.order(order)
					.storeMenu(menu)
					.quantity(count.intValue()) // count를 quantity로
					.build());
		});

		// Cart 삭제
		carts.forEach(Cart::softDelete);

		return order.getId(); // 생성된 Order ID 반환
	}

	/** 조회 **/
	// 주문 결제 전 화면 조회
	@Transactional(readOnly = true)
	public ResCheckOutOrderDto getCheckoutOrder(User user) {
		return calculateCheckout(user);
	}

	// Customer, Owner: 전체 주문 내역 조회
	@Transactional(readOnly = true)
	public Page<ResGetListOrderDto> getOrdersByUser(User user, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

		// DB에서 Role 기반 필터링 후 조회
		Page<Order> orders = switch (user.getRole()) {
			case CUSTOMER -> {
				Customer customer = customerRepository.findByUserId(user.getId())
					.orElseThrow(() -> new IllegalStateException("고객 정보가 존재하지 않습니다."));
				yield orderRepository.findByCustomerId(customer.getId(), pageable);
			}
			case OWNER -> {
				Owner owner = ownerRepository.findByUserId(user.getId())
					.orElseThrow(() -> new IllegalStateException("가게 주인 정보가 존재하지 않습니다."));
				yield orderRepository.findByStoreOwnerId(owner.getId(), pageable);
			}
			default -> throw new IllegalArgumentException("지원하지 않는 사용자 유형입니다.");
		};

		return orders.map(order -> {
			int totalPrice = calculateTotalPrice(order);
			return ResGetListOrderDto.from(order, totalPrice);
		});
	}

	// 주문 상세 조회
	@Transactional(readOnly = true)
	public ResGetOrderDto getOrderById(User user, UUID orderId) {
		// 단건 주문 조회
		Order order = findOrderOrThrow(orderId);

		// 권한 체크
		validateRoleAccess(user, order);

		// totalPrice 계산
		int totalPrice = calculateTotalPrice(order);

		return ResGetOrderDto.from(order, totalPrice);
	}

	/** 수정 **/
	// 주문 상태 변경
	@Transactional
	public void updateOrderStatus(User user, UUID orderId, ReqUpdateOrderStatusDto reqUpdateOrderStatusDto) {
		// 단건 주문 조회
		Order order = findOrderOrThrow(orderId);

		// 권한 확인: 이 주문의 가게 주인인지 확인
		if (!order.getStore().getOwner().getUser().getId().equals(user.getId())) {
			throw new IllegalStateException("Owner만 주문 상태를 변경할 수 있습니다.");
		}

		order.updateOrderStatus(user, reqUpdateOrderStatusDto);

		orderRepository.save(order);
	}

	/** 삭제 **/
	@Transactional
	public void deleteOrder(User user, UUID orderId) {
		// 단건 주문 조회
		Order order = findOrderOrThrow(orderId);

		// 본인 주문인지 확인
		if (!order.getCustomer().getUser().getId().equals(user.getId())) {
			throw new IllegalStateException("본인 주문만 삭제 가능합니다.");
		}

		// 진행 중 주문(ORDERED)은 내역 삭제 불가
		if (order.getOrderStatus() == OrderStatus.ORDERED) {
			throw new IllegalStateException("진행 중인 주문은 삭제할 수 없습니다.");
		}

		// soft delete 실행
		order.softDelete(user.getId());
		orderRepository.save(order);
	}

	/** --------------------- Helper --------------------- **/
	// Owner 본인 확인 || Manager 검증
	private void validateRoleAccess(User user, Order order) {
		boolean isOwner = order.getStore().getOwner().getUser().getId().equals(user.getId());
		boolean isManager = user.getRole() == UserRoleEnum.MANAGER;

		if (!(isOwner || isManager)) {
			throw new IllegalStateException("접근 권한이 없습니다.");
		}
	}

	// Cart 기반 Checkout 계산
	private ResCheckOutOrderDto calculateCheckout(User user) {
		Customer customer = getCustomerOrThrow(user);
		List<Cart> carts = cartRepository.findAllByCustomerIdAndDeletedAtIsNull(customer.getId());
		if (carts.isEmpty()) throw new IllegalStateException("장바구니가 비어 있습니다.");

		// 단일 매장 검증
		UUID storeId = carts.get(0).getMenu().getStore().getId();
		boolean isSingleStore = carts.stream().allMatch(c -> c.getMenu().getStore().getId().equals(storeId));
		if (!isSingleStore) throw new IllegalStateException("여러 매장 메뉴 혼합 불가");

		Address defaultAddress = addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(user.getId())
			.orElseThrow(() -> new IllegalStateException("기본 주소 정보가 없습니다."));

		int menusPrice = carts.stream().mapToInt(c -> c.getMenu().getPrice()).sum();

		int deliveryFee = carts.get(0).getMenu().getStore().getDeliveryFee();

		return ResCheckOutOrderDto.from(customer, defaultAddress, menusPrice, deliveryFee);
	}

	// Cart 에서 Store 가져오기
	private Store getStoreFromCart(User user) {
		Customer customer = getCustomerOrThrow(user);
		List<Cart> carts = cartRepository.findAllByCustomerIdAndDeletedAtIsNull(customer.getId());
		if (carts.isEmpty()) throw new IllegalStateException("장바구니가 비어 있습니다.");
		return carts.get(0).getMenu().getStore();
	}

	// User 로 Customer 조회
	private Customer getCustomerOrThrow(User user) {
		return customerRepository.findByUserId(user.getId())
			.orElseThrow(() -> new IllegalArgumentException("고객 정보가 존재하지 않습니다."));
	}

	// 단건 주문 조회
	private Order findOrderOrThrow(UUID orderId) {
		return orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
	}

	// 총 가격 계산: storeMenu.getPrice * 중복된 OrderMenu 의 row 수 = totalPrice
	private int calculateTotalPrice(Order order) {
		return order.getOrderMenus().stream()
			.collect(Collectors.groupingBy(OrderMenu::getStoreMenu, Collectors.counting()))
			.entrySet().stream()
			.mapToInt(e -> e.getKey().getPrice() * e.getValue().intValue())
			.sum();
	}
}
