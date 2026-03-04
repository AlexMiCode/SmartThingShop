package com.smartthingshop.productservice.service;

import com.smartthingshop.productservice.domain.ProductEntity;
import com.smartthingshop.productservice.dto.ProductRequest;
import com.smartthingshop.productservice.dto.ProductResponse;
import com.smartthingshop.productservice.exception.NotFoundException;
import com.smartthingshop.productservice.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse create(ProductRequest request) {
        ProductEntity entity = toEntity(request);
        return toResponse(repository.save(entity));
    }

    public ProductResponse findById(Long id) {
        ProductEntity product = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        return toResponse(product);
    }

    @Cacheable("products")
    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return repository.findByPriceBetween(minPrice, maxPrice).stream().map(this::toResponse).toList();
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse update(Long id, ProductRequest request) {
        ProductEntity found = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        found.setName(request.name());
        found.setCategory(request.category());
        found.setPrice(request.price());
        found.setStock(request.stock());
        found.setImageUrl(request.imageUrl());
        return toResponse(repository.save(found));
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateStock(Long id, Integer stock) {
        ProductEntity found = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        found.setStock(stock);
        return toResponse(repository.save(found));
    }

    @CacheEvict(value = "products", allEntries = true)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Product not found: " + id);
        }
        repository.deleteById(id);
    }

    private ProductEntity toEntity(ProductRequest request) {
        ProductEntity entity = new ProductEntity();
        entity.setName(request.name());
        entity.setCategory(request.category());
        entity.setPrice(request.price());
        entity.setStock(request.stock());
        entity.setImageUrl(request.imageUrl());
        return entity;
    }

    private ProductResponse toResponse(ProductEntity entity) {
        return new ProductResponse(entity.getId(), entity.getName(), entity.getCategory(), entity.getPrice(), entity.getStock(), entity.getImageUrl());
    }
}
