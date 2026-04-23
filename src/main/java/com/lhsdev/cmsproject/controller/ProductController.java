package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "상품", description = "상품 조회 API")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록 조회", description = "전체 상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts()); // Optionally filter active
    }

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id)); // Optionally check if active
    }
}
