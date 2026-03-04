package com.smartthingshop.notificationservice.service;

import com.smartthingshop.notificationservice.domain.NotificationEntity;
import com.smartthingshop.notificationservice.dto.NotificationRequest;
import com.smartthingshop.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService service;

    @Test
    void createShouldPersistNotification() {
        NotificationEntity entity = sample(1L, 1L, "created");
        when(repository.save(any(NotificationEntity.class))).thenReturn(entity);
        assertEquals(1L, service.create(new NotificationRequest(1L, "created")).id());
    }

    @Test
    void findAllShouldReturnEntities() {
        when(repository.findAll()).thenReturn(List.of(sample(1L, 1L, "m1"), sample(2L, 2L, "m2")));
        assertEquals(2, service.findAll().size());
    }

    @Test
    void createShouldPreserveUserId() {
        NotificationEntity entity = sample(11L, 55L, "msg");
        when(repository.save(any(NotificationEntity.class))).thenReturn(entity);
        assertEquals(55L, service.create(new NotificationRequest(55L, "msg")).userId());
    }

    @Test
    void createShouldPreserveMessage() {
        NotificationEntity entity = sample(13L, 9L, "hello");
        when(repository.save(any(NotificationEntity.class))).thenReturn(entity);
        assertEquals("hello", service.create(new NotificationRequest(9L, "hello")).message());
    }

    @Test
    void findAllShouldBeEmptyWhenRepoEmpty() {
        when(repository.findAll()).thenReturn(List.of());
        assertEquals(0, service.findAll().size());
    }

    private NotificationEntity sample(Long id, Long userId, String message) {
        NotificationEntity e = new NotificationEntity();
        e.setId(id);
        e.setUserId(userId);
        e.setMessage(message);
        e.setCreatedAt(java.time.Instant.now());
        return e;
    }
}
