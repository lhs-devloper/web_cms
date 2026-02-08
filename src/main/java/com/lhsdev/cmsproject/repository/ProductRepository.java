package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByCreatedAtDesc();

    // Find active products by category
    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);
}
