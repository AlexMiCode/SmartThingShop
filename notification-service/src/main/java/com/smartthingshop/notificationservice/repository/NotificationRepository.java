package com.smartthingshop.notificationservice.repository;

import com.smartthingshop.notificationservice.domain.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
