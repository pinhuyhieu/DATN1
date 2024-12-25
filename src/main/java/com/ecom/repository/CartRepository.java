package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {

	/**
	 * Tìm Cart theo productId và customerId
	 */
	public Cart findByProductIdAndCustomerId(Integer productId, Integer customerId);

	/**
	 * Đếm số lượng Cart theo customerId
	 */
	public Integer countByCustomerId(Integer customerId);

	/**
	 * Lấy danh sách Cart theo customerId
	 */
	public List<Cart> findByCustomerId(Integer customerId);

}
