package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.product.Product;
import com.lhsdev.cmsproject.domain.product.ProductCategory;
import com.lhsdev.cmsproject.repository.ProductCategoryRepository;
import com.lhsdev.cmsproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // --- Category Logic ---
    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    public List<ProductCategory> getActiveCategories() {
        return categoryRepository.findAllByIsActiveTrueOrderBySortOrderAsc();
    }

    @Transactional
    public void saveCategory(ProductCategory category) {
        if (category.getId() != null) {
            ProductCategory existing = categoryRepository.findById(category.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + category.getId()));
            existing.setName(category.getName());
            existing.setSortOrder(category.getSortOrder());
            existing.setIsActive(category.getIsActive());
        } else {
            categoryRepository.save(category);
        }
    }

    @Transactional
    public void deleteCategory(Long id) {
        // TODO: Check if any products exist in this category before deleting
        categoryRepository.deleteById(id);
    }

    public ProductCategory getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    // --- Product Logic ---
    public List<Product> getAllProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc();
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    @Transactional
    public void saveProduct(Product product, Long categoryId, MultipartFile file) throws IOException {
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = uploadFile(file);
        }

        if (product.getId() != null) {
            // Update
            Product existing = productRepository.findById(product.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            // Keep old image if new one is not provided
            if (imageUrl == null) {
                imageUrl = existing.getImageUrl();
            }

            existing.setCategory(category);
            existing.setName(product.getName());
            existing.setDescription(product.getDescription());
            existing.setPrice(product.getPrice());
            existing.setImageUrl(imageUrl);
            existing.setStockType(product.getStockType());
            existing.setStockQuantity(product.getStockQuantity());
            existing.setIsActive(product.getIsActive());
            // existing.setProductType(product.getProductType());

            /*
             * // Update Program Schedule if type is PROGRAM
             * if (product.getProductType() ==
             * com.lhsdev.cmsproject.domain.product.ProductType.PROGRAM) {
             * existing.setProgramSchedule(product.getProgramSchedule());
             * } else {
             * existing.setProgramSchedule(null); // Clear schedule if switched to NORMAL
             * }
             */
        } else {
            // Create
            product.setCategory(category);
            if (imageUrl != null) {
                product.setImageUrl(imageUrl);
            }
            /*
             * // ProgramSchedule is already set in product via @ModelAttribute if present
             * if (product.getProductType() ==
             * com.lhsdev.cmsproject.domain.product.ProductType.PROGRAM) {
             * if (product.getProgramSchedule() != null) {
             * product.setProgramSchedule(product.getProgramSchedule()); // Ensure
             * bidirectional relationship
             * }
             * } else {
             * product.setProgramSchedule(null);
             * }
             */
            productRepository.save(product);
        }
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private String uploadFile(MultipartFile file) throws IOException {
        String basePath = uploadDir;
        if (!basePath.endsWith("/") && !basePath.endsWith("\\")) {
            basePath += "/";
        }

        Path uploadPath = Paths.get(basePath + "products");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(storedFilename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Product Image Saved: " + filePath.toAbsolutePath());

        return "/uploads/products/" + storedFilename;
    }
}
