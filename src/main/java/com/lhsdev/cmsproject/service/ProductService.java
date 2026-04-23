package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.product.Product;
import com.lhsdev.cmsproject.domain.product.ProductCategory;
import com.lhsdev.cmsproject.dto.ProductDto;
import com.lhsdev.cmsproject.repository.ProductCategoryRepository;
import com.lhsdev.cmsproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    @Transactional
    public Product saveProduct(ProductDto dto) {
        ProductCategory category = productCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .category(category)
                .stockQuantity(dto.getStockQuantity())
                .rentalAvailableCount(dto.getRentalAvailableCount())
                .imageUrls(dto.getImageUrls())
                .active(dto.isActive())
                .pointReward(dto.getPointReward())
                .build();
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductDto dto) {
        Product product = getProduct(id);
        ProductCategory category = productCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        product.update(
                dto.getName(),
                dto.getPrice(),
                dto.getDescription(),
                category,
                dto.getStockQuantity(),
                dto.getRentalAvailableCount(),
                dto.getImageUrls(),
                dto.isActive(),
                dto.getPointReward());
        return product;
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
