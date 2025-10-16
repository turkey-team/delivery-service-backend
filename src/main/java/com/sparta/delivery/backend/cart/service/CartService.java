package com.sparta.delivery.backend.cart.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sparta.delivery.backend.cart.dto.ReqCreateCartDto;
import com.sparta.delivery.backend.cart.dto.ResCreateCartDto;
import com.sparta.delivery.backend.cart.dto.ResDeleteCartItemDto;
import com.sparta.delivery.backend.cart.dto.ResDeleteCartsDto;
import com.sparta.delivery.backend.cart.dto.ResGetCartDto;
import com.sparta.delivery.backend.cart.entity.Cart;
import com.sparta.delivery.backend.cart.repository.CartRepository;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.repository.StoreMenuRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;
	private final StoreRepository storeRepository;
	private final StoreMenuRepository storeMenuRepository;
	private final CustomerRepository customerRepository;

	@Transactional
	public ResCreateCartDto createCart(User user, ReqCreateCartDto requestDto) {

		StoreMenu menu = storeMenuRepository.findByIdAndDeletedAtIsNull(requestDto.getMenuId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 메뉴가 존재하지 않습니다."));
		Store store = storeRepository.findByIdAndDeletedAtIsNull(menu.getStore().getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"가게가 존재하지 않습니다."));
		Customer customer = customerRepository.findByUserIdAndDeletedAtIsNull(user.getId()).orElseThrow(()->new AccessDeniedException("로그인 후 사용해주세요"));

		// cart 비었는지 확인
		boolean isNotEmpty = checkIsNotEmpty(customer);

		// 카트가 비어있지 않다면
		if (isNotEmpty) {
			// 같은 가게의 메뉴가 담겨있는지 확인
			hasActiveCartInStore(store.getId());
		}

		Cart cart = Cart.builder()
			.customer(customer)
			.menu(menu)
			.build();

		Cart savedCart = cartRepository.save(cart);
		ResCreateCartDto resoponseDto = new ResCreateCartDto(savedCart.getId(), savedCart.getMenu().getName());

		return resoponseDto;
	}

	@Transactional(readOnly = true)
	public ResGetCartDto getCarts(User user) {
		Customer customer = customerRepository.findByUserIdAndDeletedAtIsNull(user.getId()).orElseThrow(()->new AccessDeniedException("로그인 후 사용해주세요"));

		ResGetCartDto responseDto = cartRepository.findCartGroupByMenu(customer.getId());

		return responseDto;
	}

	@Transactional
	public ResDeleteCartItemDto deleteCartItem(User user, UUID cartId) {
		Cart cart = cartRepository.findByIdWithCustomerAndUser(cartId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"존재하지 않는 메뉴이거나 이미 삭제되었습니다."));

		// 로그인한 유저와 카트의 유저 아이디가 다를 경우
		if (!cart.getCustomer().getUser().getId().equals(user.getId())) {
			throw new  AccessDeniedException("올바른 접근이 아닙니다");
		}

		// 메뉴 row 삭제
		cart.softDelete(user.getId());
		Cart savedCart = cartRepository.save(cart);

		return new ResDeleteCartItemDto(savedCart.getId(),savedCart.getMenu().getName());
	}

	@Transactional
	public ResDeleteCartsDto deleteCarts(User user) {
		Customer customer = customerRepository.findByUserIdAndDeletedAtIsNull(user.getId()).orElseThrow(()->new AccessDeniedException("로그인 후 사용해주세요"));

		List<Cart> carts = cartRepository.findAllByCustomerIdAndDeletedAtIsNull(customer.getId());

		for(Cart cart : carts) {
			cart.softDelete(user.getId());
		}

		List<Cart> savedCarts = cartRepository.saveAll(carts);

		return new ResDeleteCartsDto(customer.getId(),savedCarts.get(0).getMenu().getStore().getName());

	}

	public boolean checkIsNotEmpty(Customer customer) {
		return cartRepository.existsByCustomerIdAndDeletedAtIsNull(customer.getId());
	}

	private void hasActiveCartInStore(UUID storeId) {
		boolean checkSameStore = cartRepository.existsByDeletedAtIsNullAndMenuStoreId(storeId);
		if (!checkSameStore) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"장바구니에는 같은 가게의 메뉴만 담을 수 있습니다.");
		}
	}
}
