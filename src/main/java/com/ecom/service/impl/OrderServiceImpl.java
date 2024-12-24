package com.ecom.service.impl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ecom.model.*;
import com.ecom.repository.ProductRepository;
import com.ecom.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.service.OrderService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ProductOrderRepository orderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private VoucherService voucherService;

	@Override
	public void saveOrder(Integer userid, OrderRequest orderRequest) throws Exception {

		List<Cart> carts = cartRepository.findByUserId(userid);

		// Tính tổng giá trị giỏ hàng
		double totalPrice = carts.stream()
				.mapToDouble(cart -> cart.getProduct().getDiscountPrice() * cart.getQuantity())
				.sum();

		// Áp dụng mã giảm giá nếu có
		Double discount = 0.0;
		if (orderRequest.getVoucherCode() != null && !orderRequest.getVoucherCode().isEmpty()) {
			discount = voucherService.applyVoucher(orderRequest.getVoucherCode(), totalPrice);
		}

		// Tổng tiền sau giảm giá
		double totalAfterDiscount = totalPrice - discount;

		for (Cart cart : carts) {
			Product product = cart.getProduct();

			// Kiểm tra số lượng tồn kho trước khi đặt hàng
			if (cart.getQuantity() > product.getStock()) {
				throw new RuntimeException("Không đủ số lượng sản phẩm " + product.getTitle() + " trong kho.");
			}

			// Tạo đơn hàng
			ProductOrder order = new ProductOrder();

			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(LocalDate.now());

			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());

			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());

			order.setStatus(OrderStatus.IN_PROGRESS.getName());
			order.setPaymentType(orderRequest.getPaymentType());
			order.setDiscountedPrice(totalAfterDiscount);

			// Lưu thông tin địa chỉ
			OrderAddress address = new OrderAddress();
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setState(orderRequest.getState());
			address.setPincode(orderRequest.getPincode());

			order.setOrderAddress(address);

			// Lưu đơn hàng vào cơ sở dữ liệu
			ProductOrder saveOrder = orderRepository.save(order);

			// Giảm số lượng tồn kho sản phẩm
			product.setStock(product.getStock() - cart.getQuantity());
			productRepository.save(product);

			// Gửi email xác nhận đơn hàng
			commonUtil.sendMailForProductOrder(saveOrder, "success");
		}

		// Xóa giỏ hàng sau khi đặt hàng thành công
		cartRepository.deleteAll(carts);
	}


	@Override
	public List<ProductOrder> getOrdersByUser(Integer userId) {
		List<ProductOrder> orders = orderRepository.findByUserId(userId);
		return orders;
	}

	@Override
	public ProductOrder updateOrderStatus(Integer id, String status) {
		Optional<ProductOrder> findById = orderRepository.findById(id);
		if (findById.isPresent()) {
			ProductOrder productOrder = findById.get();
			productOrder.setStatus(status);
			ProductOrder updateOrder = orderRepository.save(productOrder);
			return updateOrder;
		}
		return null;
	}

	@Override
	public List<ProductOrder> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return orderRepository.findAll(pageable);

	}

	@Override
	public ProductOrder getOrdersByOrderId(String orderId) {
		return orderRepository.findByOrderId(orderId);
	}

}
