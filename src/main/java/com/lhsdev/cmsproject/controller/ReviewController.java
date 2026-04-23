package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.config.auth.PrincipalDetails;
import com.lhsdev.cmsproject.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "리뷰", description = "상품 리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "배송 완료된 주문의 상품에 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<?> createReview(@AuthenticationPrincipal PrincipalDetails principal,
                                          @RequestBody Map<String, Object> request) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            Long orderId = ((Number) request.get("orderId")).longValue();
            Long productId = ((Number) request.get("productId")).longValue();
            int rating = ((Number) request.get("rating")).intValue();
            String content = (String) request.getOrDefault("content", "");

            var review = reviewService.createReview(principal.getUser(), orderId, productId, rating, content);
            return ResponseEntity.ok(Map.of("message", "리뷰가 등록되었습니다.", "reviewId", review.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "상품 리뷰 조회", description = "상품의 리뷰 목록을 조회합니다.")
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @Operation(summary = "내 리뷰 조회", description = "내가 작성한 리뷰 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<?> getMyReviews(@AuthenticationPrincipal PrincipalDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        return ResponseEntity.ok(reviewService.getMyReviews(principal.getUser().getId()));
    }

    @Operation(summary = "리뷰 삭제", description = "본인이 작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@AuthenticationPrincipal PrincipalDetails principal,
                                          @PathVariable Long reviewId) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            reviewService.deleteReview(principal.getUser(), reviewId);
            return ResponseEntity.ok(Map.of("message", "리뷰가 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
