package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.cart.CartItem;
import com.lhsdev.cmsproject.domain.order.Order;
import com.lhsdev.cmsproject.domain.order.OrderItem;
import com.lhsdev.cmsproject.domain.order.OrderStatus;
import com.lhsdev.cmsproject.domain.payment.Payment;
import com.lhsdev.cmsproject.domain.product.Product;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.dto.OrderCreateRequest;
import com.lhsdev.cmsproject.dto.OrderDto;
import com.lhsdev.cmsproject.dto.OrderItemDto;
import com.lhsdev.cmsproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MembershipService membershipService;

    @Transactional
    public OrderDto createOrder(User user, OrderCreateRequest request) {
        // 1. CartItem 목록 조회
        List<CartItem> cartItems;
        if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
            cartItems = cartItemRepository.findAllById(request.getCartItemIds()).stream()
                    .filter(item -> item.getUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());
        } else {
            cartItems = cartItemRepository.findByUserOrderByCreatedAtDesc(user);
        }

        // 2. 빈 장바구니 검증
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("장바구니가 비어있습니다.");
        }

        // 3. 주문번호 생성
        String orderNumber = generateOrderNumber();

        // 4. Order 생성
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(0)
                .orderNumber(orderNumber)
                .build();

        int totalPrice = 0;

        // 5. 각 CartItem 검증 및 OrderItem 생성
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (!product.isActive()) {
                throw new IllegalStateException("판매 중지된 상품이 포함되어 있습니다: " + product.getName());
            }

            if (product.getCategory().isHasStock()) {
                product.decreaseStock(cartItem.getQuantity());
            }
            if (product.getCategory().isHasRentalPeriod()) {
                product.decreaseRentalCount(cartItem.getQuantity());
                // 대여 기간 중복 검증
                if (cartItem.getRentalStartDate() != null && cartItem.getRentalEndDate() != null) {
                    List<OrderItem> overlapping = orderItemRepository.findOverlappingRentals(
                            product.getId(), cartItem.getRentalStartDate(), cartItem.getRentalEndDate());
                    if (!overlapping.isEmpty()) {
                        throw new IllegalStateException("대여 기간이 중복됩니다: " + product.getName());
                    }
                }
            }

            int subtotal = product.getPrice() * cartItem.getQuantity();
            totalPrice += subtotal;

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .snapshotPrice(product.getPrice())
                    .snapshotProductName(product.getName())
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .productType(product.getCategory().getCode())
                    .rentalStartDate(cartItem.getRentalStartDate())
                    .rentalEndDate(cartItem.getRentalEndDate())
                    .build();

            order.addOrderItem(orderItem);
        }

        // 6. totalPrice 설정
        order.updateTotalPrice(totalPrice);

        // 7. Order 저장
        orderRepository.save(order);

        // 8. 사용한 CartItem 삭제
        cartItemRepository.deleteAll(cartItems);

        return toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getMyOrders(User user, int size) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .limit(size)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getMyOrder(User user, Long orderId) {
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        return toDto(order);
    }

    @Transactional
    public void cancelOrder(User user, Long orderId) {
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("취소할 수 없는 주문 상태입니다.");
        }

        // 재고 복원
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            if (product.getCategory().isHasStock()) {
                product.increaseStock(item.getQuantity());
            }
            if (product.getCategory().isHasRentalPeriod()) {
                product.increaseRentalCount(item.getQuantity());
            }
        }

        order.cancel();
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        return toDto(order);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        order.updateStatus(status);

        // CONFIRMED로 변경 시 포인트 적립
        if (status == OrderStatus.CONFIRMED) {
            int totalPoints = 0;
            for (var item : order.getOrderItems()) {
                Product product = item.getProduct();
                totalPoints += product.getPointReward() * item.getQuantity();
            }
            if (totalPoints > 0) {
                membershipService.addPoints(order.getUser().getId(), totalPoints);
            }
        }
    }

    @Transactional
    public void updateTracking(Long orderId, String trackingNumber, String carrier, String memo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        order.updateTracking(trackingNumber, carrier, memo);
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = ThreadLocalRandom.current().nextInt(10000, 99999);
        return "ORD-" + date + "-" + random;
    }

    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus().name());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setUserName(order.getUser().getName());
        dto.setUserEmail(order.getUser().getEmail());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderItemDto> itemDtos = order.getOrderItems().stream().map(item -> {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getSnapshotProductName());
            itemDto.setPrice(item.getSnapshotPrice());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setSubtotal(item.getSubtotal());
            itemDto.setProductType(item.getProductType());
            // 상품 이미지 정보
            Product product = item.getProduct();
            List<String> images = product.getImageUrls();
            itemDto.setImageUrls(images);
            itemDto.setImageUrl(images != null && !images.isEmpty() ? images.get(0) : "");
            itemDto.setRentalStartDate(item.getRentalStartDate());
            itemDto.setRentalEndDate(item.getRentalEndDate());
            return itemDto;
        }).collect(Collectors.toList());
        dto.setOrderItems(itemDtos);

        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setTrackingCarrier(order.getTrackingCarrier());
        dto.setTrackingMemo(order.getTrackingMemo());
        dto.setShippedAt(order.getShippedAt());

        // Payment 정보 조회
        paymentRepository.findByOrderId(order.getId()).ifPresent(payment -> {
            dto.setPaymentMethod(payment.getPaymentMethod().name());
            dto.setPaymentStatus(payment.getStatus().name());
        });

        return dto;
    }
}
