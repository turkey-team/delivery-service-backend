package com.sparta.delivery.backend.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.sparta.delivery.backend.cart.dto.ReqCreateCartDto;
import com.sparta.delivery.backend.cart.dto.ResCreateCartDto;
import com.sparta.delivery.backend.cart.dto.ResDeleteCartsDto;
import com.sparta.delivery.backend.cart.entity.Cart;
import com.sparta.delivery.backend.cart.repository.CartRepository;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.repository.StoreMenuRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;

@Disabled
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CartServiceTest {

	@InjectMocks
	private CartService cartService;

	@Mock
	private CartRepository cartRepository;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private StoreMenuRepository storeMenuRepository;
	@Mock
	private CustomerRepository customerRepository;

	private UUID customerId;
	private Long userId;
	private UUID storeId;
	private UUID menuId;
	private User mockUser;
	private Customer mockCustomer;
	private Store mockStore;
	private StoreMenu mockMenu;


	@BeforeEach
	void setUp() {

		customerId = UUID.randomUUID();
		userId = 1L;
		storeId = UUID.randomUUID();
		menuId = UUID.randomUUID();

		mockUser = mock(User.class);
		when(mockUser.getId()).thenReturn(userId);

		mockCustomer = mock(Customer.class);
		when(mockCustomer.getId()).thenReturn(customerId);
		// when(mockCustomer.getUser()).thenReturn(mockUser);

		mockStore = mock(Store.class);
		lenient().when(mockStore.getId()).thenReturn(storeId);
		lenient().when(mockStore.getName()).thenReturn("테스트 가게");

		mockMenu = mock(StoreMenu.class);
		// when(mockMenu.getId()).thenReturn(menuId);
		when(mockMenu.getName()).thenReturn("테스트메뉴");
		when(mockMenu.getStore()).thenReturn(mockStore);

		when(customerRepository.findByUserId(anyLong())).thenReturn(Optional.of(mockCustomer));

	}

	@Test
	@DisplayName("Cart 생성 : 비어있는 카트에 메뉴 추가 성공")
	void createCart_Success_EmptyCart(){
		// given
		ReqCreateCartDto reqCreateCartDto = new ReqCreateCartDto(menuId);
		Cart mockSavedCart = mock(Cart.class);

		UUID expectedCartId = UUID.fromString("6b0b4a23-225a-4bd7-87ae-90bdb2da5177");

		when(mockSavedCart.getId()).thenReturn(expectedCartId);
		when(mockSavedCart.getMenu()).thenReturn(mockMenu);

		when(cartRepository.save(any(Cart.class))).thenReturn(mockSavedCart);

		when(storeMenuRepository.findById(menuId)).thenReturn(Optional.of(mockMenu));
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
		when(cartRepository.existsByCustomerIdAndDeletedAtIsNull(customerId)).thenReturn(Boolean.FALSE); // isNotEmpty = FALSE

		// when
		ResCreateCartDto result = cartService.createCart(mockUser, reqCreateCartDto);

		// then
		assertNotNull(result, "Service method returned null DTO.");

		assertNotNull(result.getCartId(), "CartId in the result DTO is null.");
		assertEquals(expectedCartId, result.getCartId());
		assertEquals("테스트메뉴", result.getMenuName());

		// 검증
		verify(cartRepository, never()).existsByDeletedAtIsNullAndMenuStoreId(any());
		verify(cartRepository, times(1)).save(any(Cart.class));
	}

	@Test
	@DisplayName("Cart 생성: 비어있지 않은 카트에 같은 가게 메뉴 추가 성공")
	void createCart_Success_SameStore() {
		// given
		ReqCreateCartDto reqCreateCartDto = new ReqCreateCartDto(menuId);
		Cart mockSavedCart = mock(Cart.class);
		UUID cartId = UUID.randomUUID();

		when(storeMenuRepository.findById(menuId)).thenReturn(Optional.of(mockMenu));
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));

		when(cartRepository.existsByCustomerIdAndDeletedAtIsNull(customerId)).thenReturn(Boolean.TRUE);

		when(cartRepository.existsByDeletedAtIsNullAndMenuStoreId(storeId)).thenReturn(Boolean.TRUE);

		when(cartRepository.save(any(Cart.class))).thenReturn(mockSavedCart);

		when(mockSavedCart.getId()).thenReturn(cartId);
		when(mockSavedCart.getMenu()).thenReturn(mockMenu);

		// when
		ResCreateCartDto result = cartService.createCart(mockUser, reqCreateCartDto);

		// then
		assertNotNull(result);
		assertEquals(cartId, result.getCartId());

		// 검증
		verify(cartRepository, times(1)).existsByDeletedAtIsNullAndMenuStoreId(storeId);
		verify(cartRepository, times(1)).save(any(Cart.class));
	}

	@Test
	@DisplayName("Cart 생성: 다른 가게 메뉴 추가 시 예외 발생")
	void createCart_Fail_DifferentStore() {
		// given
		ReqCreateCartDto reqCreateCartDto = new ReqCreateCartDto(menuId);

		when(storeMenuRepository.findById(menuId)).thenReturn(Optional.of(mockMenu));
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));

		// 다른 가게 카트가 존재
		when(cartRepository.existsByCustomerIdAndDeletedAtIsNull(customerId)).thenReturn(Boolean.TRUE);
		when(cartRepository.existsByDeletedAtIsNullAndMenuStoreId(storeId)).thenReturn(Boolean.FALSE);

		// when & then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			cartService.createCart(mockUser, reqCreateCartDto);
		});

		assertEquals("장바구니에는 같은 가게의 메뉴만 담을 수 있습니다.", exception.getMessage());

		// 검증
		verify(cartRepository, never()).save(any(Cart.class));
	}

	@Test
	@DisplayName("장바구니 전체 삭제 성공")
	void deleteCarts_Success() {
		// given
		Cart mockCart1 = mock(Cart.class);
		Cart mockCart2 = mock(Cart.class);

		when(mockCart1.getMenu()).thenReturn(mockMenu);

		List<Cart> mockCarts = List.of(mockCart1, mockCart2);

		when(cartRepository.findAllByCustomerIdAndDeletedAtIsNull(customerId)).thenReturn(mockCarts);
		when(cartRepository.saveAll(mockCarts)).thenReturn(mockCarts);

		// when
		ResDeleteCartsDto result = cartService.deleteCarts(mockUser);

		// then
		assertNotNull(result);
		assertEquals(customerId, result.getCustomerId());
		assertEquals("테스트 가게", result.getStoreName()); // mockStore.getName() 반환

		// 검증
		verify(mockCart1, times(1)).softDelete();
		verify(mockCart2, times(1)).softDelete();
		verify(cartRepository, times(1)).saveAll(mockCarts);
	}
}
