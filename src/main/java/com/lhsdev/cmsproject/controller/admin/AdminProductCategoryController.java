package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.product.ProductCategory;
import com.lhsdev.cmsproject.repository.ProductCategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/product-categories")
@RequiredArgsConstructor
@Tag(name = "관리자상품카테고리", description = "상품 타입/카테고리 관리 API (관리자 전용)")
public class AdminProductCategoryController {
    private final ProductCategoryRepository repository;

    @Operation(summary = "전체 상품 카테고리 목록", description = "모든 상품 카테고리를 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(repository.findAllByOrderBySortOrderAsc());
    }

    @Operation(summary = "상품 카테고리 생성", description = "새 상품 카테고리를 생성합니다.")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductCategory category) {
        return ResponseEntity.ok(repository.save(category));
    }

    @Operation(summary = "상품 카테고리 수정", description = "기존 상품 카테고리를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductCategory dto) {
        ProductCategory cat = repository.findById(id).orElseThrow();
        cat.setCode(dto.getCode());
        cat.setName(dto.getName());
        cat.setDescription(dto.getDescription());
        cat.setHasStock(dto.isHasStock());
        cat.setHasRentalPeriod(dto.isHasRentalPeriod());
        cat.setActive(dto.isActive());
        cat.setSortOrder(dto.getSortOrder());
        return ResponseEntity.ok(repository.save(cat));
    }

    @Operation(summary = "상품 카테고리 삭제", description = "상품 카테고리를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
