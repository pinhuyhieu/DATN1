package com.ecom.service.impl;

import com.ecom.model.Cart;
import com.ecom.model.Customer;
import com.ecom.model.Product;
import com.ecom.repository.CartRepository;
import com.ecom.repository.CustomerRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Cart saveCart(Integer productId, Integer customerId) {
		// Kiểm tra Customer
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + customerId));

		// Kiểm tra Product
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));

		// Tìm sản phẩm trong giỏ hàng
		Cart existingCart = cartRepository.findByProductIdAndCustomerId(productId, customerId);

		// Xử lý thêm mới hoặc cập nhật giỏ hàng
		Cart cart = ObjectUtils.isEmpty(existingCart) ? new Cart() : existingCart;

		if (cart.getId() == null) { // Nếu giỏ hàng chưa tồn tại
			if (product.getStock() < 1) {
				throw new RuntimeException("Sản phẩm " + product.getTitle() + " đã hết hàng.");
			}
			cart.setCustomer(customer);
			cart.setProduct(product);
			cart.setQuantity(1);
		} else { // Nếu giỏ hàng đã tồn tại
			if (cart.getQuantity() + 1 > product.getStock()) {
				throw new RuntimeException("Không đủ số lượng tồn kho cho sản phẩm " + product.getTitle());
			}
			cart.setQuantity(cart.getQuantity() + 1);
		}

		// Tính tổng giá
		cart.setTotalPrice(cart.getQuantity() * product.getDiscountPrice());

		// Lưu giỏ hàng
		return cartRepository.save(cart);
	}

	@Override
	public List<Cart> getCartsByCustomer(Integer customerId) {
		List<Cart> carts = cartRepository.findByCustomerId(customerId);
		double totalOrderPrice = 0.0;

		for (Cart cart : carts) {
			double totalPrice = cart.getProduct().getDiscountPrice() * cart.getQuantity();
			cart.setTotalPrice(totalPrice);
			totalOrderPrice += totalPrice;
			cart.setTotalOrderPrice(totalOrderPrice); // Tổng giá trị giỏ hàng
		}

		return carts;
	}

	@Override
	public Integer getCountCart(Integer customerId) {
		return cartRepository.countByCustomerId(customerId);
	}

	@Override
	public void updateQuantity(String action, Integer cartId) {
		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng với ID: " + cartId));

		int updatedQuantity = cart.getQuantity();

		if ("decrease".equalsIgnoreCase(action)) {
			updatedQuantity -= 1;
			if (updatedQuantity <= 0) {
				cartRepository.delete(cart);
				return;
			}
		} else if ("increase".equalsIgnoreCase(action)) {
			if (updatedQuantity + 1 > cart.getProduct().getStock()) {
				throw new RuntimeException("Không đủ số lượng tồn kho.");
			}
			updatedQuantity += 1;
		}

		cart.setQuantity(updatedQuantity);
		cart.setTotalPrice(updatedQuantity * cart.getProduct().getDiscountPrice());
		cartRepository.save(cart);
	}

	@Override
	public void removeCart(Integer cartId) {
		cartRepository.deleteById(cartId);
	}
}
