package com.smartthingshop.orderservice.controller;

import com.smartthingshop.orderservice.domain.OrderEntity;
import com.smartthingshop.orderservice.domain.OrderStatus;
import com.smartthingshop.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:orderdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "eureka.client.enabled=false"
})
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository repository;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void listByUserShouldReadFromDb() throws Exception {
        OrderEntity order = new OrderEntity();
        order.setUserId(50L);
        order.setProductId(2L);
        order.setQuantity(1);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(Instant.now());
        repository.save(order);

        mockMvc.perform(get("/api/orders").param("userId", "50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value(50));
    }

    @Test
    void changeStatusShouldReturnUpdatedOrder() throws Exception {
        OrderEntity order = new OrderEntity();
        order.setUserId(1L);
        order.setProductId(2L);
        order.setQuantity(1);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(Instant.now());
        OrderEntity saved = repository.save(order);

        mockMvc.perform(patch("/api/orders/" + saved.getId() + "/status").param("status", "PAID").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PAID"));
    }
}
