package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.order.OrderStatus;
import com.lhsdev.cmsproject.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Tag(name = "관리자주문관리", description = "주문 관리 API (관리자 전용)")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "전체 주문 목록", description = "모든 주문을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrder(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "주문 상태 변경", description = "주문의 상태를 변경합니다.")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id,
                                               @RequestBody Map<String, String> request) {
        try {
            OrderStatus status = OrderStatus.valueOf(request.get("status"));
            orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "배송 트래킹 정보 업데이트", description = "운송장 번호, 택배사, 메모를 업데이트합니다.")
    @PatchMapping("/{id}/tracking")
    public ResponseEntity<?> updateTracking(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            orderService.updateTracking(id, request.get("trackingNumber"), request.get("trackingCarrier"), request.get("trackingMemo"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
