package com.smartthingshop.userservice.repository;

import com.smartthingshop.userservice.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
