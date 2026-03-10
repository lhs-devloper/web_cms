package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.cart.CartItem;
import com.lhsdev.cmsproject.domain.product.Product;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.dto.CartItemDto;
import com.lhsdev.cmsproject.repository.CartItemRepository;
import com.lhsdev.cmsproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItems(User user) {
        List<CartItem> items = cartItemRepository.findByUserOrderByCreatedAtDesc(user);
        return items.stream().map(item -> {
            CartItemDto dto = new CartItemDto();
            dto.setId(item.getId());
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setProductPrice(item.getProduct().getPrice());
            dto.setProductType(item.getProduct().getType().name());
            dto.setImageUrl(item.getProduct().getImageUrls() != null && !item.getProduct().getImageUrls().isEmpty()
                    ? item.getProduct().getImageUrls().get(0)
                    : "");
            dto.setQuantity(item.getQuantity());
            dto.setRentalStartDate(item.getRentalStartDate());
            dto.setRentalEndDate(item.getRentalEndDate());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void addCartItem(User user, CartItemDto req) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.isActive()) {
            throw new IllegalStateException("Product is not active");
        }

        // Check if item is already in cart
        var existing = cartItemRepository.findByUserAndProductId(user, product.getId());
        if (existing.isPresent()) {
            CartItem cartItem = existing.get();
            cartItem.updateQuantity(cartItem.getQuantity() + req.getQuantity());
            if (req.getRentalStartDate() != null && req.getRentalEndDate() != null) {
                cartItem.updateRentalDates(req.getRentalStartDate(), req.getRentalEndDate());
            }
        } else {
            CartItem newItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(req.getQuantity())
                    .rentalStartDate(req.getRentalStartDate())
                    .rentalEndDate(req.getRentalEndDate())
                    .build();
            cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void updateCartItemQuantity(User user, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.updateQuantity(quantity);
        }
    }

    @Transactional
    public void deleteCartItem(User user, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized");
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
}
