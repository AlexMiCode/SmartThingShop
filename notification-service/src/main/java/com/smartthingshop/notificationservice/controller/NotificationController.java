package com.smartthingshop.notificationservice.controller;

import com.smartthingshop.notificationservice.dto.NotificationRequest;
import com.smartthingshop.notificationservice.dto.NotificationResponse;
import com.smartthingshop.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/internal")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse createInternal(@Valid @RequestBody NotificationRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<NotificationResponse> findAll() {
        return service.findAll();
    }
}
