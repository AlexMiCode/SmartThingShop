package com.smartthingshop.userservice.service;

import com.smartthingshop.userservice.domain.UserEntity;
import com.smartthingshop.userservice.dto.UserRequest;
import com.smartthingshop.userservice.dto.UserResponse;
import com.smartthingshop.userservice.exception.NotFoundException;
import com.smartthingshop.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserResponse create(UserRequest request) {
        UserEntity entity = toEntity(request);
        UserEntity saved = repository.save(entity);
        log.info("Created user id={} email={}", saved.getId(), saved.getEmail());
        return toResponse(saved);
    }

    public UserResponse findById(Long id) {
        UserEntity found = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found: " + id));
        return toResponse(found);
    }

    public List<UserResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse update(Long id, UserRequest request) {
        UserEntity found = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found: " + id));
        found.setFullName(request.fullName());
        found.setEmail(request.email());
        found.setRole(request.role());
        return toResponse(repository.save(found));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("User not found: " + id);
        }
        repository.deleteById(id);
        log.info("Deleted user id={}", id);
    }

    private UserEntity toEntity(UserRequest request) {
        UserEntity entity = new UserEntity();
        entity.setFullName(request.fullName());
        entity.setEmail(request.email());
        entity.setRole(request.role());
        return entity;
    }

    private UserResponse toResponse(UserEntity entity) {
        return new UserResponse(entity.getId(), entity.getFullName(), entity.getEmail(), entity.getRole());
    }
}
