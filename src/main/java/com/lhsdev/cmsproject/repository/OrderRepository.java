package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.order.Order;
import com.lhsdev.cmsproject.domain.order.OrderStatus;
import com.lhsdev.cmsproject.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByIdAndUser(Long id, User user);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findAllByOrderByCreatedAtDesc();
}
