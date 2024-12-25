package com.ecom.service;

import com.ecom.model.Orders;
import org.springframework.data.domain.Page;


import java.util.List;

public interface OrderService {
	void saveOrder(Integer customerId, Orders order) throws Exception;

	List<Orders> getOrdersByCustomer(Integer customerId);

	Orders updateOrderStatus(Integer orderId, Integer statusId);

	List<Orders> getAllOrders();

	Orders getOrdersByOrderId(String orderId);

	Page<Orders> getAllOrdersPagination(Integer pageNo, Integer pageSize);
}
