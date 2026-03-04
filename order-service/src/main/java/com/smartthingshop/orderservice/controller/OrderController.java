package com.smartthingshop.orderservice.controller;

import com.smartthingshop.orderservice.domain.OrderStatus;
import com.smartthingshop.orderservice.dto.CreateOrderRequest;
import com.smartthingshop.orderservice.dto.OrderResponse;
import com.smartthingshop.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<OrderResponse> byUser(@RequestParam("userId") Long userId) {
        return service.findByUserId(userId);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse changeStatus(@PathVariable("id") Long id, @RequestParam("status") OrderStatus status) {
        return service.changeStatus(id, status);
    }
}
