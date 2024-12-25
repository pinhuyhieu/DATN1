package com.ecom.repository;

import com.ecom.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByCustomerId(Integer customerId);
    Optional<Orders> findByOrderId(String orderId);
}
