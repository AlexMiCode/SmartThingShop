package com.smartthingshop.productservice.repository;

import com.smartthingshop.productservice.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}
