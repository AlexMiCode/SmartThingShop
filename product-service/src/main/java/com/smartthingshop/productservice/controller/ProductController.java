package com.smartthingshop.productservice.controller;

import com.smartthingshop.productservice.dto.ProductRequest;
import com.smartthingshop.productservice.dto.ProductResponse;
import com.smartthingshop.productservice.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<ProductResponse> findAll(@RequestParam(required = false) BigDecimal minPrice,
                                         @RequestParam(required = false) BigDecimal maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return service.findByPriceRange(minPrice, maxPrice);
        }
        return service.findAll();
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable("id") Long id, @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/stock")
    public ProductResponse updateStock(@PathVariable("id") Long id, @RequestParam("stock") @Min(0) Integer stock) {
        return service.updateStock(id, stock);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
