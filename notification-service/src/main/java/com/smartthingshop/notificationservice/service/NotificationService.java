package com.smartthingshop.notificationservice.service;

import com.smartthingshop.notificationservice.domain.NotificationEntity;
import com.smartthingshop.notificationservice.dto.NotificationRequest;
import com.smartthingshop.notificationservice.dto.NotificationResponse;
import com.smartthingshop.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public NotificationResponse create(NotificationRequest request) {
        NotificationEntity entity = new NotificationEntity();
        entity.setUserId(request.userId());
        entity.setMessage(request.message());
        entity.setCreatedAt(Instant.now());

        NotificationEntity saved = repository.save(entity);
        log.info("Notification created id={} userId={}", saved.getId(), saved.getUserId());
        return toResponse(saved);
    }

    public List<NotificationResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    private NotificationResponse toResponse(NotificationEntity entity) {
        return new NotificationResponse(entity.getId(), entity.getUserId(), entity.getMessage(), entity.getCreatedAt());
    }
}
