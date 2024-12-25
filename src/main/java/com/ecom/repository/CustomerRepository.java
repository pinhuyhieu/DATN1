package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	/**
	 * Tìm khách hàng theo email
	 */
	Customer findByEmail(String email);

	/**
	 * Lấy danh sách khách hàng theo vai trò
	 */
	List<Customer> findByRole(String role);

	/**
	 * Tìm khách hàng theo reset token
	 */
	Customer findByResetToken(String token);

	/**
	 * Kiểm tra xem email có tồn tại không
	 */
	Boolean existsByEmail(String email);
}
