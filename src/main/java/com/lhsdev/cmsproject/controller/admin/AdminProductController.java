package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.dto.ProductDto;
import com.lhsdev.cmsproject.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "관리자상품관리", description = "상품 관리 API (관리자 전용)")
public class AdminProductController {

    private final ProductService productService;

    @Operation(summary = "전체 상품 목록", description = "모든 상품을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @Operation(summary = "상품 등록", description = "새 상품을 등록합니다.")
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDto productDto) {
        try {
            return ResponseEntity.ok(productService.saveProduct(productDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, productDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
