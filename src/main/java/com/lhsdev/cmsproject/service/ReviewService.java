package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.order.Order;
import com.lhsdev.cmsproject.domain.order.OrderStatus;
import com.lhsdev.cmsproject.domain.product.Product;
import com.lhsdev.cmsproject.domain.review.Review;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.OrderRepository;
import com.lhsdev.cmsproject.repository.ProductRepository;
import com.lhsdev.cmsproject.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Review createReview(User user, Long orderId, Long productId, int rating, String content) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("본인의 주문에만 리뷰를 작성할 수 있습니다.");
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송 완료된 주문에만 리뷰를 작성할 수 있습니다.");
        }

        if (reviewRepository.existsByOrderIdAndProductId(orderId, productId)) {
            throw new IllegalStateException("이미 리뷰를 작성한 상품입니다.");
        }

        boolean hasProduct = order.getOrderItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
        if (!hasProduct) {
            throw new IllegalStateException("해당 주문에 포함되지 않은 상품입니다.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1~5 사이여야 합니다.");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .order(order)
                .rating(rating)
                .content(content)
                .build();

        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(r -> Map.<String, Object>of(
                        "id", r.getId(),
                        "userName", r.getUser().getName(),
                        "rating", r.getRating(),
                        "content", r.getContent() != null ? r.getContent() : "",
                        "createdAt", r.getCreatedAt().toString()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMyReviews(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(r -> Map.<String, Object>of(
                        "id", r.getId(),
                        "productId", r.getProduct().getId(),
                        "productName", r.getProduct().getName(),
                        "rating", r.getRating(),
                        "content", r.getContent() != null ? r.getContent() : "",
                        "createdAt", r.getCreatedAt().toString()
                ))
                .toList();
    }

    @Transactional
    public void deleteReview(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("본인의 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }
}
