package com.smartthingshop.orderservice.service;

import com.smartthingshop.orderservice.domain.OrderEntity;
import com.smartthingshop.orderservice.domain.OrderStatus;
import com.smartthingshop.orderservice.dto.CreateOrderRequest;
import com.smartthingshop.orderservice.dto.OrderResponse;
import com.smartthingshop.orderservice.exception.BusinessException;
import com.smartthingshop.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository repository;
    private final RestTemplate restTemplate;
    private final String productServiceUrl;
    private final String notificationServiceUrl;

    public OrderService(OrderRepository repository,
                        RestTemplate restTemplate,
                        @Value("${services.product.url:http://product-service:8082}") String productServiceUrl,
                        @Value("${services.notification.url:http://notification-service:8084}") String notificationServiceUrl) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
        this.notificationServiceUrl = notificationServiceUrl;
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "createWithFallback")
    public OrderResponse create(CreateOrderRequest request) {
        assertProductExists(request.productId());
        OrderEntity entity = new OrderEntity();
        entity.setUserId(request.userId());
        entity.setProductId(request.productId());
        entity.setQuantity(request.quantity());
        entity.setStatus(OrderStatus.NEW);
        entity.setCreatedAt(Instant.now());

        OrderEntity saved = repository.save(entity);
        sendNotification(saved.getId(), saved.getUserId());
        return toResponse(saved);
    }

    public OrderResponse createWithFallback(CreateOrderRequest request, Throwable throwable) {
        log.warn("Fallback for order creation user={} product={}", request.userId(), request.productId(), throwable);
        throw new BusinessException("Product service is temporarily unavailable. Try later.");
    }

    public List<OrderResponse> findByUserId(Long userId) {
        return repository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    public OrderResponse changeStatus(Long orderId, OrderStatus status) {
        OrderEntity entity = repository.findById(orderId)
            .orElseThrow(() -> new BusinessException("Order not found: " + orderId));
        entity.setStatus(status);
        return toResponse(repository.save(entity));
    }

    private void assertProductExists(Long productId) {
        try {
            HttpStatusCode code = restTemplate.getForEntity(productServiceUrl + "/api/products/" + productId, String.class).getStatusCode();
            if (!code.is2xxSuccessful()) {
                throw new BusinessException("Product is not available: " + productId);
            }
        } catch (RestClientException ex) {
            throw new BusinessException("Product check failed: " + ex.getMessage());
        }
    }

    private void sendNotification(Long orderId, Long userId) {
        try {
            restTemplate.postForEntity(notificationServiceUrl + "/api/notifications/internal",
                Map.of("message", "Order " + orderId + " has been created", "userId", userId),
                Void.class);
        } catch (Exception ex) {
            log.warn("Unable to send notification for order={}", orderId);
        }
    }

    private OrderResponse toResponse(OrderEntity entity) {
        return new OrderResponse(entity.getId(), entity.getUserId(), entity.getProductId(), entity.getQuantity(), entity.getStatus(), entity.getCreatedAt());
    }
}
