package com.ecom.service;

import com.ecom.model.OrderStatus;

import java.util.Optional;

public interface OrderStatusService {
    /**
     * Lấy trạng thái đơn hàng theo ID.
     *
     * @param id ID trạng thái đơn hàng
     * @return Optional<OrderStatus>
     */
    Optional<OrderStatus> getOrderStatusById(Integer id);
}
