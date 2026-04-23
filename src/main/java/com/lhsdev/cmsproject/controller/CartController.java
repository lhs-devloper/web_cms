package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.config.auth.PrincipalDetails;
import com.lhsdev.cmsproject.dto.CartItemDto;
import com.lhsdev.cmsproject.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "장바구니", description = "장바구니 관리 API")
public class CartController {
    @Autowired
    private CartService cartService;

    @Operation(summary = "장바구니 조회", description = "현재 사용자의 장바구니 상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getCartItems(@AuthenticationPrincipal PrincipalDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }
        return ResponseEntity.ok(cartService.getCartItems(principal.getUser()));
    }

    @Operation(summary = "장바구니 추가", description = "상품을 장바구니에 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity<?> addCartItem(@AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody CartItemDto req) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            cartService.addCartItem(principal.getUser(), req);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "수량 변경", description = "장바구니 상품의 수량을 변경합니다.")
    @PutMapping("/{cartItemId}")
    public ResponseEntity<?> updateQuantity(@AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Integer> req) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            cartService.updateCartItemQuantity(principal.getUser(), cartItemId, req.get("quantity"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "장바구니 항목 삭제", description = "장바구니에서 특정 상품을 삭제합니다.")
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long cartItemId) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            cartService.deleteCartItem(principal.getUser(), cartItemId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "장바구니 비우기", description = "장바구니의 모든 상품을 삭제합니다.")
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal PrincipalDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        cartService.clearCart(principal.getUser());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
