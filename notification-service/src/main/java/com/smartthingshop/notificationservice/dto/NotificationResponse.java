package com.smartthingshop.notificationservice.dto;

import java.time.Instant;

public record NotificationResponse(Long id, Long userId, String message, Instant createdAt) {}
