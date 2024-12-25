package com.ecom.service.impl;

import com.ecom.model.*;
import com.ecom.repository.*;
import com.ecom.service.OrderService;
import com.ecom.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class OrdersServiceImpl implements OrderService {

    @Autowired
    private OrderRepository ordersRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Override
    public void saveOrder(Integer customerId, Orders orderData) throws Exception {
        List<Cart> carts = cartRepository.findByCustomerId(customerId);

        if (carts.isEmpty()) {
            throw new RuntimeException("Giỏ hàng của bạn trống, không thể tạo đơn hàng.");
        }

        double totalAmount = 0.0;

        for (Cart cart : carts) {
            Product product = cart.getProduct();

            // Kiểm tra tồn kho
            if (cart.getQuantity() > product.getStock()) {
                throw new RuntimeException("Không đủ số lượng tồn kho cho sản phẩm: " + product.getTitle());
            }

            totalAmount += cart.getQuantity() * product.getDiscountPrice();

            // Giảm số lượng tồn kho
            product.setStock(product.getStock() - cart.getQuantity());
            productRepository.save(product);
        }

        // Tạo đơn hàng
        Orders order = new Orders();
        order.setCustomer(orderData.getCustomer());
        order.setInvoice(orderData.getInvoice());
        order.setAddress(orderData.getAddress());
        order.setShippingMethod(orderData.getShippingMethod());
        order.setPaymentMethod(orderData.getPaymentMethod());
        order.setTotalAmount(totalAmount);
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        order.setStatus(orderData.getStatus());

        // Lưu đơn hàng
        ordersRepository.save(order);

        // Xóa giỏ hàng sau khi đặt hàng
        cartRepository.deleteAll(carts);

        // Gửi email xác nhận
        commonUtil.sendMailForOrder(order, "success");
    }

    @Override
    public List<Orders> getOrdersByCustomer(Integer customerId) {
        return ordersRepository.findByCustomerId(customerId);
    }

    @Override
    public Orders updateOrderStatus(Integer orderId, Integer statusId) {
        // Tìm kiếm đơn hàng theo ID
        Optional<Orders> optionalOrder = ordersRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Orders order = optionalOrder.get();

            // Tìm trạng thái đơn hàng mới dựa trên statusId
            OrderStatus newStatus = orderStatusRepository.findById(statusId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái với ID: " + statusId));

            // Cập nhật trạng thái đơn hàng
            order.setStatus(newStatus);
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            // Lưu đơn hàng với trạng thái mới
            return ordersRepository.save(order);
        }
        throw new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId);
    }


    @Override
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    @Override
    public Orders getOrdersByOrderId(String orderId) {
        return ordersRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với mã: " + orderId));
    }
    @Override
    public Page<Orders> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Orders> ordersPage = ordersRepository.findAll(pageable);
        ordersPage.forEach(order -> {
            order.getInvoice().getId(); // Đảm bảo load dữ liệu hóa đơn
        });
        return ordersPage;
    }
}
