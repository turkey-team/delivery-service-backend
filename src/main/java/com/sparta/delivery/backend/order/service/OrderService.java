package com.sparta.delivery.backend.order.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.address.dto.ResAddressDto;
import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.address.repository.AddressRepository;
import com.sparta.delivery.backend.address.service.AddressService;
import com.sparta.delivery.backend.cart.entity.Cart;
import com.sparta.delivery.backend.cart.repository.CartRepository;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.order.dto.ReqCreateOrderDto;
import com.sparta.delivery.backend.order.dto.ReqUpdateOrderStatusDto;
import com.sparta.delivery.backend.order.dto.ResGetListOrderDto;
import com.sparta.delivery.backend.order.dto.ResGetOrderDto;
import com.sparta.delivery.backend.order.entity.Order;
import com.sparta.delivery.backend.order.enums.OrderStatus;
import com.sparta.delivery.backend.order.repository.OrderMenuRepository;
import com.sparta.delivery.backend.order.repository.OrderRepository;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.menu.repository.StoreMenuRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
import com.sparta.delivery.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final CartRepository cartRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final StoreRepository storeRepository;
	private final AddressRepository addressRepository;
	private final DongRepository dongRepository;
	private final OrderMenuRepository orderMenuRepository; // OrderMenu 저장용
	private final StoreMenuRepository storeMenuRepository; // Menu 조회용

	private final AddressService addressService;

	@Transactional
	public void createOrder(User user, UUID storeId, ReqCreateOrderDto reqCreateOrderDto) {
		// 주문할 고객 조회
		Customer customer = customerRepository.findByUserId(user.getId())
			.orElseThrow(() -> new IllegalStateException("고객 정보가 존재하지 않습니다."));

		// 장바구니 조회
		List<Cart> carts = cartRepository.findAllByCustomerIdAndStoreIdAndDeletedAtIsNull(customer.getId(), storeId);
		if (carts.isEmpty()) throw new IllegalStateException("장바구니가 비어 있습니다.");

		// 장바구니에 담긴 메뉴별 수량 계산
		Map<UUID, Long> menuCount = carts.stream()
			.collect(Collectors.groupingBy(cart -> cart.getMenu().getId(), Collectors.counting()));

		// 대표 주소 조회
		UserDetailsImpl userDetailsImpl = new UserDetailsImpl(user);
		ResAddressDto defaultAddress = addressService.getDefaultAddress(userDetailsImpl);

		// Address 엔티티에서 Dong 객체 가져오기
		Address addressEntity = addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(user.getId())
			.orElseThrow(() -> new IllegalStateException("기본 주소 정보가 없습니다."));

		// 주문 생성
		Store store = storeRepository.getReferenceById(storeId);
		Order order = Order.builder()
			.store(store)
			.customer(customer)
			.dongEntity(addressEntity.getDong())
			.gu(addressEntity.getDong().getSigungu().getName())
			.dong(addressEntity.getDong().getName())
			.addressDetails(defaultAddress.getAddress())
			.orderStatus(OrderStatus.ORDERED)
			.requestMessage(reqCreateOrderDto.getRequestMessage())
			.payMethod(reqCreateOrderDto.getPayMethod())
			.build();
		Order savedOrder = orderRepository.save(order);

		// 주문 상세 생성 & 장바구니 비우기
		carts.forEach(cart -> {
			orderItemRepository.save(new OrderItem(savedOrder, cart.getMenu(), cart.getMenu().getPrice()));
			cart.softDelete();
		});
		cartRepository.saveAll(carts);
	}

	@Transactional(readOnly = true)
	public Page<ResGetListOrderDto> getOrdersByUserId(User user, int page, int size) {
		userRepository.findById(user.getId())
			.orElseThrow(() -> new IllegalStateException("고객 정보가 존재하지 않습니다."));
		return orderRepository.findAllByCustomerId(customer.getId(), PageRequest.of(page, size))
			.map(ResGetListOrderDto::from);
	}

	@Transactional(readOnly = true)
	public ResGetOrderDto getOrderByStoreIdAndOrderId(User user, UUID storeId, UUID orderId) {
		Order order = orderRepository.findByIdAndStoreId(orderId, storeId)
			.orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
		validateRoleAccess(user, order); // 소유권/권한 검증
		return ResGetOrderDto.from(order);
	}

	@Transactional
	public void updateOrderStatus(User user, UUID storeId, UUID orderId, ReqUpdateOrderStatusDto req) {
		Order order = orderRepository.findByIdAndStoreId(orderId, storeId)
			.orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
		validateRoleAccess(user, order); // Owner/Manager 권한 확인

		order.setStatus(req.getStatus());
		if (req.getStatus() == OrderStatus.CANCELLED) {
			order.setCancelledAt(Instant.now());
			order.setCancelledBy(user.getId());
			order.setCancelledReason(req.getCancelledReason());
		}
		orderRepository.save(order);
	}

	@Transactional
	public void deleteOrder(User user, UUID storeId, UUID orderId) {
		Order order = orderRepository.findByIdAndStoreId(orderId, storeId)
			.orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));

		if (!order.getCustomer().getUser().getId().equals(user.getId())) {
			throw new IllegalStateException("본인 주문만 삭제 가능합니다.");
		}

		order.setStatus(OrderStatus.CANCELLED);
		order.setCancelledAt(Instant.now());
		order.setCancelledBy(user.getId());
		orderRepository.save(order);
	}

	/** --------------------- Helper --------------------- **/
	// Owner 본인 확인 || Manager 검증
	private void validateRoleAccess(User user, Order order) {
		boolean isOwner = order.getStore().getOwner().getUser().getId().equals(user.getId());
		boolean isManager = user.getRole() == UserRoleEnum.MANAGER;

		if (!(isManager || isOwner)) {
			throw new IllegalStateException("권한이 없습니다.");
		}
	}
}
