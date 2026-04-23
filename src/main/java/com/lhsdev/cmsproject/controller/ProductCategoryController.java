package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.repository.ProductCategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
@Tag(name = "상품카테고리", description = "상품 타입/카테고리 조회 API")
public class ProductCategoryController {
    private final ProductCategoryRepository repository;

    @Operation(summary = "활성 상품 카테고리 목록", description = "활성화된 상품 카테고리 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getActive() {
        return ResponseEntity.ok(repository.findByIsActiveTrueOrderBySortOrderAsc());
    }
}
