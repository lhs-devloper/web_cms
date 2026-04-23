package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.domain.order.Order;
import com.lhsdev.cmsproject.domain.payment.PaymentMethod;
import com.lhsdev.cmsproject.config.auth.PrincipalDetails;
import com.lhsdev.cmsproject.dto.PaymentApproveRequest;
import com.lhsdev.cmsproject.repository.OrderRepository;
import com.lhsdev.cmsproject.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "결제", description = "결제 처리 API")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private com.lhsdev.cmsproject.repository.PaymentConfigRepository paymentConfigRepository;

    @Autowired
    private com.lhsdev.cmsproject.repository.PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Operation(summary = "결제 설정 조회", description = "활성화된 결제 수단 설정을 조회합니다.")
    @GetMapping("/configs")
    public ResponseEntity<?> getActiveConfigs() {
        return ResponseEntity.ok(paymentConfigRepository.findByIsActiveTrue().stream().map(config -> 
            Map.of(
                "provider", config.getProvider(),
                "clientKey", config.getClientKey() != null ? config.getClientKey() : "",
                "displayName", config.getDisplayName()
            )
        ).collect(Collectors.toList()));
    }

    @Operation(summary = "최근 결제 수단 조회", description = "사용자의 마지막 결제 수단을 조회합니다.")
    @GetMapping("/last-method")
    public ResponseEntity<?> getLastPaymentMethod(@AuthenticationPrincipal PrincipalDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        var results = paymentRepository.findLatestPaidByUserId(principal.getUser().getId(), PageRequest.of(0, 1));
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(Map.of("paymentMethod", results.get(0).getPaymentMethod().name()));
    }

    @Operation(summary = "결제 준비", description = "주문에 대한 결제를 준비합니다.")
    @PostMapping("/{orderId}/ready")
    public ResponseEntity<?> readyPayment(@PathVariable Long orderId,
                                          @RequestBody Map<String, String> request) {
        try {
            PaymentMethod method = PaymentMethod.valueOf(request.get("paymentMethod"));
            return ResponseEntity.ok(paymentService.requestPayment(orderId, method));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "결제 승인", description = "결제를 승인 처리합니다.")
    @PostMapping("/{orderId}/approve")
    public ResponseEntity<?> approvePayment(@PathVariable Long orderId,
                                            @RequestBody PaymentApproveRequest request) {
        try {
            return ResponseEntity.ok(paymentService.approvePayment(orderId, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "카카오페이 결제 완료", description = "카카오페이 결제 완료 콜백을 처리합니다.")
    @GetMapping("/kakao/success")
    public ResponseEntity<?> kakaoSuccess(@RequestParam String orderNumber,
                                          @RequestParam("pg_token") String pgToken) {
        try {
            Order order = orderRepository.findByOrderNumber(orderNumber)
                    .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

            PaymentApproveRequest request = new PaymentApproveRequest();
            request.setPgToken(pgToken);

            var result = paymentService.approvePayment(order.getId(), request);
            return ResponseEntity.ok(Map.of("success", true, "message", "카카오페이 결제가 완료되었습니다.",
                    "orderId", order.getId(), "status", result.getStatus()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @Operation(summary = "토스페이 결제 완료", description = "토스페이 결제 완료 콜백을 처리합니다.")
    @GetMapping("/toss/success")
    public ResponseEntity<?> tossSuccess(@RequestParam String orderNumber,
                                         @RequestParam String paymentKey,
                                         @RequestParam int amount) {
        try {
            Order order = orderRepository.findByOrderNumber(orderNumber)
                    .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

            PaymentApproveRequest request = new PaymentApproveRequest();
            request.setPaymentKey(paymentKey);
            request.setAmount(amount);

            var result = paymentService.approvePayment(order.getId(), request);
            return ResponseEntity.ok(Map.of("success", true, "message", "토스 결제가 완료되었습니다.",
                    "orderId", order.getId(), "status", result.getStatus()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @Operation(summary = "KCP 결제 완료", description = "KCP 결제 완료 콜백을 처리합니다.")
    @GetMapping("/kcp/return")
    public ResponseEntity<?> kcpReturn(@RequestParam String orderNumber,
                                       @RequestParam(required = false) String tno) {
        try {
            Order order = orderRepository.findByOrderNumber(orderNumber)
                    .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

            PaymentApproveRequest request = new PaymentApproveRequest();
            request.setPgToken(tno);

            var result = paymentService.approvePayment(order.getId(), request);
            return ResponseEntity.ok(Map.of("success", true, "message", "KCP 결제가 완료되었습니다.",
                    "orderId", order.getId(), "status", result.getStatus()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
