package com.sparta.delivery.backend.cart.controller;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.cart.dto.ReqCreateCartDto;
import com.sparta.delivery.backend.cart.dto.ResCreateCartDto;
import com.sparta.delivery.backend.cart.dto.ResDeleteCartItemDto;
import com.sparta.delivery.backend.cart.dto.ResDeleteCartsDto;
import com.sparta.delivery.backend.cart.dto.ResGetCartDto;
import com.sparta.delivery.backend.cart.service.CartService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Tag(name="Cart-Controller", description = "장바구니 관련 API")
public class CartController {
	private final CartService cartService;

	@PostMapping("/carts")
	@Operation(summary = "장바구니 추가", description = "장바구니가 비었다면 새로 생성하고, 기존 장바구니가 있다면 메뉴를 추가합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "장바구니 추가 성공"
			,content = @Content(schema = @Schema(implementation = ResCreateCartDto.class)))
		,@ApiResponse(responseCode = "400", description = "잘못된 요청")
		,@ApiResponse(responseCode = "403", description = "로그인한 유저와 장바구니 유저가 동일하지 않음")
	})
	@PreAuthorize("isAuthenticated() && hasAnyRole('CUSTOMER')")
	public ResCreateCartDto createCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ReqCreateCartDto requestDto){
		return cartService.createCart(userDetails.getUser(), requestDto);
	}


	@GetMapping("/carts")
	@Operation(summary = "장바구니 조회", description = "고객의 장바구니를 조회합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "장바구니 조회 성공"
			,content = @Content(schema = @Schema(implementation = ResGetCartDto.class)))
		,@ApiResponse(responseCode = "403", description = "로그인한 유저와 장바구니 유저가 동일하지 않음")
	})
	@PreAuthorize("isAuthenticated() && hasAnyRole('CUSTOMER')")
	public ResGetCartDto getCarts(@AuthenticationPrincipal UserDetailsImpl userDetails){
		return cartService.getCarts(userDetails.getUser());
	}

	@PutMapping("/carts/{cartId}")
	@Operation(summary = "장바구니에서 메뉴 삭제", description = "장바구니에서 해당 메뉴 한 개를 삭제합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "장바구니에서 메뉴 삭제 성공"
			,content = @Content(schema = @Schema(implementation = ResDeleteCartItemDto.class)))
		,@ApiResponse(responseCode = "403", description = "로그인한 유저와 장바구니 유저가 동일하지 않음")
	})
	@PreAuthorize("isAuthenticated() && hasAnyRole('CUSTOMER')")
	public ResDeleteCartItemDto deleteCartItem(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID cartId){
		return cartService.deleteCartItem(userDetails.getUser(), cartId);
	}

	@DeleteMapping("/carts")
	@Operation(summary = "장바구니 비우기", description = "장바구니에 담긴 전체 메뉴를 삭제합니다.")
	@ApiResponses(value= {
		@ApiResponse(responseCode = "200", description = "장바구니 삭제 성공"
			,content = @Content(schema = @Schema(implementation = ResDeleteCartsDto.class)))
		,@ApiResponse(responseCode = "403", description = "로그인한 유저와 장바구니 유저가 동일하지 않음")
	})
	@PreAuthorize("isAuthenticated() && hasAnyRole('CUSTOMER')")
	public ResDeleteCartsDto deleteCarts(@AuthenticationPrincipal UserDetailsImpl userDetails){
		return cartService.deleteCarts(userDetails.getUser());
	}

}
