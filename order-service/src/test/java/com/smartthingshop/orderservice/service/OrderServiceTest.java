package com.smartthingshop.orderservice.service;

import com.smartthingshop.orderservice.domain.OrderEntity;
import com.smartthingshop.orderservice.domain.OrderStatus;
import com.smartthingshop.orderservice.dto.CreateOrderRequest;
import com.smartthingshop.orderservice.exception.BusinessException;
import com.smartthingshop.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private RestTemplate restTemplate;

    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderService(repository, restTemplate, "http://product-service:8082", "http://notification-service:8084");
    }

    @Test
    void createShouldPersistWhenProductExists() {
        when(restTemplate.getForEntity(contains("/api/products/"), eq(String.class)))
            .thenReturn(ResponseEntity.ok("{}"));
        when(repository.save(any(OrderEntity.class))).thenReturn(sample(12L, OrderStatus.NEW));

        var response = service.create(new CreateOrderRequest(1L, 2L, 1));

        assertEquals(12L, response.id());
    }

    @Test
    void createShouldThrowWhenProductCheckFails() {
        when(restTemplate.getForEntity(contains("/api/products/"), eq(String.class)))
            .thenThrow(new RestClientException("product down"));

        assertThrows(BusinessException.class, () -> service.create(new CreateOrderRequest(1L, 2L, 1)));
    }

    @Test
    void findByUserShouldMapResponseList() {
        when(repository.findByUserId(50L)).thenReturn(List.of(sample(1L, OrderStatus.NEW)));
        assertEquals(1, service.findByUserId(50L).size());
    }

    @Test
    void changeStatusShouldUpdateOrder() {
        OrderEntity existing = sample(3L, OrderStatus.NEW);
        when(repository.findById(3L)).thenReturn(Optional.of(existing));
        when(repository.save(any(OrderEntity.class))).thenAnswer(a -> a.getArgument(0));

        var updated = service.changeStatus(3L, OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, updated.status());
    }

    @Test
    void fallbackShouldThrowBusinessException() {
        var request = new CreateOrderRequest(1L, 2L, 1);
        assertThrows(BusinessException.class, () -> service.createWithFallback(request, new RuntimeException("down")));
    }

    private OrderEntity sample(Long id, OrderStatus status) {
        OrderEntity e = new OrderEntity();
        e.setId(id);
        e.setUserId(1L);
        e.setProductId(2L);
        e.setQuantity(1);
        e.setStatus(status);
        e.setCreatedAt(Instant.now());
        return e;
    }
}
