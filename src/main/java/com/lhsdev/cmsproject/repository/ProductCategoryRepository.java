package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.product.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Optional<ProductCategory> findByCode(String code);
    List<ProductCategory> findByIsActiveTrueOrderBySortOrderAsc();
    List<ProductCategory> findAllByOrderBySortOrderAsc();
}
