package com.sparta.delivery.backend.cart.repository;

import java.util.UUID;

import com.sparta.delivery.backend.cart.dto.ResGetCartDto;

public interface CartRepositoryCustom {
	ResGetCartDto findCartGroupByMenu(UUID cusomerId);
}
