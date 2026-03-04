package com.smartthingshop.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
    @NotNull Long userId,
    @NotBlank String message
) {}
