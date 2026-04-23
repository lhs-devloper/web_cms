package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.product.ProductCategory;
import com.lhsdev.cmsproject.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/product-categories")
@RequiredArgsConstructor
public class AdminProductCategoryController {
    private final ProductCategoryRepository repository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(repository.findAllByOrderBySortOrderAsc());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductCategory category) {
        return ResponseEntity.ok(repository.save(category));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
