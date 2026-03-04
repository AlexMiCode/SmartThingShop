package com.smartthingshop.orderservice.dto;

import com.smartthingshop.orderservice.domain.OrderStatus;

import java.time.Instant;

public record OrderResponse(
    Long id,
    Long userId,
    Long productId,
    Integer quantity,
    OrderStatus status,
    Instant createdAt
) {}
