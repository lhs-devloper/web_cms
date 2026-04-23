package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.config.auth.PrincipalDetails;
import com.lhsdev.cmsproject.dto.OrderCreateRequest;
import com.lhsdev.cmsproject.service.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "주문", description = "주문 관리 API")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "주문 생성", description = "장바구니 상품으로 새 주문을 생성합니다.")
    @PostMapping
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal PrincipalDetails principal,
                                         @RequestBody OrderCreateRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            return ResponseEntity.ok(orderService.createOrder(principal.getUser(), request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "내 주문 목록", description = "현재 사용자의 주문 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getMyOrders(@AuthenticationPrincipal PrincipalDetails principal,
                                         @RequestParam(defaultValue = "5") int size) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }
        return ResponseEntity.ok(orderService.getMyOrders(principal.getUser(), size));
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMyOrder(@AuthenticationPrincipal PrincipalDetails principal,
                                        @PathVariable Long id) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            return ResponseEntity.ok(orderService.getMyOrder(principal.getUser(), id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@AuthenticationPrincipal PrincipalDetails principal,
                                         @PathVariable Long id) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            orderService.cancelOrder(principal.getUser(), id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
