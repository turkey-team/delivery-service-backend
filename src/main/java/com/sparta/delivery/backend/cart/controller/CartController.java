package com.sparta.delivery.backend.cart.controller;

import java.util.UUID;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CartController {
	private final CartService cartService;

	@PostMapping("/carts")
	public ResCreateCartDto createCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody @Valid ReqCreateCartDto requestDto){
		return cartService.createCart(userDetails.getUser(), requestDto);
	}

	@GetMapping("/carts")
	public ResGetCartDto getCarts(@AuthenticationPrincipal UserDetailsImpl userDetails){
		return cartService.getCarts(userDetails.getUser());
	}

	@PutMapping("/carts/{cartId}")
	public ResDeleteCartItemDto deleteCartItem(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID cartId){
		return cartService.deleteCartItem(userDetails.getUser(), cartId);
	}

	@DeleteMapping("/carts")
	public ResDeleteCartsDto deleteCarts(@AuthenticationPrincipal UserDetailsImpl userDetails){
		return cartService.deleteCarts(userDetails.getUser());
	}

}
