package com.smartthingshop.userservice.service;

import com.smartthingshop.userservice.domain.UserEntity;
import com.smartthingshop.userservice.dto.UserRequest;
import com.smartthingshop.userservice.exception.NotFoundException;
import com.smartthingshop.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void createShouldPersistAndReturnDto() {
        UserEntity saved = new UserEntity();
        saved.setId(1L);
        saved.setFullName("John");
        saved.setEmail("john@mail.com");
        saved.setRole("CUSTOMER");
        when(repository.save(any(UserEntity.class))).thenReturn(saved);

        var response = service.create(new UserRequest("John", "john@mail.com", "CUSTOMER"));

        assertEquals(1L, response.id());
        assertEquals("John", response.fullName());
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        when(repository.findById(44L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById(44L));
    }

    @Test
    void findAllShouldMapAllRows() {
        UserEntity u1 = new UserEntity();
        u1.setId(1L);
        u1.setFullName("A");
        u1.setEmail("a@mail.com");
        u1.setRole("CUSTOMER");
        UserEntity u2 = new UserEntity();
        u2.setId(2L);
        u2.setFullName("B");
        u2.setEmail("b@mail.com");
        u2.setRole("ADMIN");
        when(repository.findAll()).thenReturn(List.of(u1, u2));

        var result = service.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void updateShouldMutateExistingEntity() {
        UserEntity existing = new UserEntity();
        existing.setId(9L);
        existing.setFullName("Old");
        existing.setEmail("old@mail.com");
        existing.setRole("CUSTOMER");

        when(repository.findById(9L)).thenReturn(Optional.of(existing));
        when(repository.save(any(UserEntity.class))).thenAnswer(a -> a.getArgument(0));

        var result = service.update(9L, new UserRequest("New", "new@mail.com", "ADMIN"));

        assertEquals("New", result.fullName());
        assertEquals("ADMIN", result.role());
    }

    @Test
    void deleteShouldCallRepositoryById() {
        when(repository.existsById(7L)).thenReturn(true);
        service.delete(7L);

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(repository).deleteById(captor.capture());
        assertEquals(7L, captor.getValue());
    }
}
