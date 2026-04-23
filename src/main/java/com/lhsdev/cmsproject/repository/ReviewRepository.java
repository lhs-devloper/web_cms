package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Review> findByOrderIdAndProductId(Long orderId, Long productId);
    boolean existsByOrderIdAndProductId(Long orderId, Long productId);
}
