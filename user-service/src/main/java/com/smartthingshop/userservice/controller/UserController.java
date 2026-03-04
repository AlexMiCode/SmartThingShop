package com.smartthingshop.userservice.controller;

import com.smartthingshop.userservice.dto.UserRequest;
import com.smartthingshop.userservice.dto.UserResponse;
import com.smartthingshop.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return service.findAll();
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable("id") Long id, @Valid @RequestBody UserRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
