package com.smartthingshop.productservice.service;

import com.smartthingshop.productservice.domain.ProductEntity;
import com.smartthingshop.productservice.dto.ProductRequest;
import com.smartthingshop.productservice.exception.NotFoundException;
import com.smartthingshop.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    void createShouldMapRequest() {
        ProductEntity saved = sample(5L);
        when(repository.save(any(ProductEntity.class))).thenReturn(saved);

        var result = service.create(req());

        assertEquals(5L, result.id());
    }

    @Test
    void findByIdShouldReturnItem() {
        when(repository.findById(1L)).thenReturn(Optional.of(sample(1L)));
        assertEquals("Smart Plug", service.findById(1L).name());
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        when(repository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById(100L));
    }

    @Test
    void findByPriceRangeShouldUseRepositoryFilter() {
        when(repository.findByPriceBetween(BigDecimal.ONE, BigDecimal.TEN)).thenReturn(List.of(sample(2L)));
        assertEquals(1, service.findByPriceRange(BigDecimal.ONE, BigDecimal.TEN).size());
    }

    @Test
    void deleteShouldThrowWhenEntityAbsent() {
        when(repository.existsById(9L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.delete(9L));
    }

    private ProductEntity sample(Long id) {
        ProductEntity e = new ProductEntity();
        e.setId(id);
        e.setName("Smart Plug");
        e.setCategory("Power");
        e.setPrice(BigDecimal.TEN);
        e.setStock(10);
        e.setImageUrl("https://example.com/plug.jpg");
        return e;
    }

    private ProductRequest req() {
        return new ProductRequest("Smart Plug", "Power", BigDecimal.TEN, 10, "https://example.com/plug.jpg");
    }
}
