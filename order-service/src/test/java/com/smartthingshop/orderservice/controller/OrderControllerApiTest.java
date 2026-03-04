package com.smartthingshop.orderservice.controller;

import com.smartthingshop.orderservice.domain.OrderStatus;
import com.smartthingshop.orderservice.dto.OrderResponse;
import com.smartthingshop.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void listShouldReturnJsonArray() throws Exception {
        Mockito.when(orderService.findByUserId(1L))
            .thenReturn(List.of(new OrderResponse(1L, 1L, 2L, 1, OrderStatus.NEW, Instant.now())));

        mockMvc.perform(get("/api/orders").param("userId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].productId").value(2));
    }
}
