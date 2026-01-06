package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.product.ProductDto;
import com.br.deliveryapi.entity.Product;
import com.br.deliveryapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(dto.toEntity()).toDto());
    }

    @GetMapping("/")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> productList = productService.findAll()
                .stream()
                .map(Product::toDto)
                .toList();

        return ResponseEntity.ok(productList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id).toDto());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,
                                                    @Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.update(id, dto.toEntity()).toDto());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
