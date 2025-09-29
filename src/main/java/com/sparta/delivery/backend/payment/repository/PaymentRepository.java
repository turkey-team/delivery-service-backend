package com.sparta.delivery.backend.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
