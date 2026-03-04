package com.smartthingshop.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
    @NotBlank String fullName,
    @Email @NotBlank String email,
    @NotBlank String role
) {}
