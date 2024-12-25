package com.ecom.service.impl;

import com.ecom.model.OrderStatus;
import com.ecom.repository.OrderStatusRepository;
import com.ecom.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderStatusServiceImpl implements OrderStatusService {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Override
    public Optional<OrderStatus> getOrderStatusById(Integer id) {
        return orderStatusRepository.findById(id);
    }
}
