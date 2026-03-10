package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.product.Product;
import com.lhsdev.cmsproject.dto.ProductDto;
import com.lhsdev.cmsproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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
        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .type(dto.getType())
                .stockQuantity(dto.getStockQuantity())
                .rentalAvailableCount(dto.getRentalAvailableCount())
                .imageUrls(dto.getImageUrls())
                .active(dto.isActive())
                .build();
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductDto dto) {
        Product product = getProduct(id);
        product.update(
                dto.getName(),
                dto.getPrice(),
                dto.getDescription(),
                dto.getType(),
                dto.getStockQuantity(),
                dto.getRentalAvailableCount(),
                dto.getImageUrls(),
                dto.isActive());
        return product;
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
