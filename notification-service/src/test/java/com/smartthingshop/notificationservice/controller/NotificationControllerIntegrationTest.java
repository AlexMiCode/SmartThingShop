package com.smartthingshop.notificationservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:notificationdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "eureka.client.enabled=false"
})
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAndReadNotifications() throws Exception {
        String payload = """
            {
              \"userId\": 1,
              \"message\": \"Order created\"
            }
            """;

        mockMvc.perform(post("/api/notifications/internal").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/notifications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void createValidationErrorShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/notifications/internal").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
    }
}
