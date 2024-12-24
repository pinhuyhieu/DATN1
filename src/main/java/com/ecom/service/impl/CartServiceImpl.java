package com.ecom.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Cart saveCart(Integer productId, Integer userId) {

		UserDtls userDtls = userRepository.findById(userId).orElseThrow(() ->
				new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
		Product product = productRepository.findById(productId).orElseThrow(() ->
				new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));

		Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

		Cart cart;

		if (ObjectUtils.isEmpty(cartStatus)) {
			// Kiểm tra tồn kho trước khi thêm sản phẩm mới vào giỏ
			if (product.getStock() < 1) {
				throw new RuntimeException("Sản phẩm " + product.getTitle() + " đã hết hàng.");
			}
			cart = new Cart();
			cart.setProduct(product);
			cart.setUser(userDtls);
			cart.setQuantity(1);
			cart.setTotalPrice(1 * product.getDiscountPrice());
		} else {
			// Kiểm tra tồn kho khi tăng số lượng trong giỏ hàng
			if (cartStatus.getQuantity() + 1 > product.getStock()) {
				throw new RuntimeException("Không đủ số lượng tồn kho cho sản phẩm " + product.getTitle());
			}
			cart = cartStatus;
			cart.setQuantity(cart.getQuantity() + 1);
			cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
		}

		// Lưu thông tin giỏ hàng vào cơ sở dữ liệu
		return cartRepository.save(cart);
	}


	@Override
	public List<Cart> getCartsByUser(Integer userId) {
		List<Cart> carts = cartRepository.findByUserId(userId);

		Double totalOrderPrice = 0.0;
		List<Cart> updateCarts = new ArrayList<>();
		for (Cart c : carts) {
			Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());
			c.setTotalPrice(totalPrice);
			totalOrderPrice = totalOrderPrice + totalPrice;
			c.setTotalOrderPrice(totalOrderPrice);
			updateCarts.add(c);
		}

		return updateCarts;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		Integer countByUserId = cartRepository.countByUserId(userId);
		return countByUserId;
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {

		Cart cart = cartRepository.findById(cid).get();
		int updateQuantity;

		if (sy.equalsIgnoreCase("de")) {
			updateQuantity = cart.getQuantity() - 1;

			if (updateQuantity <= 0) {
				cartRepository.delete(cart);
			} else {
				cart.setQuantity(updateQuantity);
				cartRepository.save(cart);
			}

		} else {
			updateQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updateQuantity);
			cartRepository.save(cart);
		}

	}


}
