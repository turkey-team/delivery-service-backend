package com.sparta.delivery.backend.cart.service;

import org.springframework.stereotype.Service;

import com.sparta.delivery.backend.cart.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;

}
