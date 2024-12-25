package com.ecom.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Customer;

public interface CustomerService {

	/**
	 * Lưu thông tin khách hàng mới
	 */
	Customer saveCustomer(Customer customer);

	/**
	 * Tìm khách hàng theo email
	 */
	Customer getCustomerByEmail(String email);

	/**
	 * Lấy danh sách khách hàng theo vai trò
	 */
	List<Customer> getCustomers(String role);

	/**
	 * Cập nhật trạng thái tài khoản (kích hoạt hoặc vô hiệu hóa)
	 */
	Boolean updateAccountStatus(Integer id, Boolean status);

	/**
	 * Tăng số lần thất bại khi đăng nhập
	 */
	void increaseFailedAttempt(Customer customer);

	/**
	 * Khóa tài khoản khách hàng
	 */
	void customerAccountLock(Customer customer);

	/**
	 * Mở khóa tài khoản nếu hết thời gian khóa
	 */
	boolean unlockAccountTimeExpired(Customer customer);

	/**
	 * Đặt lại số lần thất bại khi đăng nhập
	 */
	void resetAttempt(int customerId);

	/**
	 * Cập nhật token để đặt lại mật khẩu
	 */
	void updateCustomerResetToken(String email, String resetToken);

	/**
	 * Tìm khách hàng theo token
	 */
	Customer getCustomerByToken(String token);

	/**
	 * Cập nhật thông tin khách hàng
	 */
	Customer updateCustomer(Customer customer);

	/**
	 * Cập nhật hồ sơ khách hàng, bao gồm ảnh đại diện
	 */
	Customer updateCustomerProfile(Customer customer, MultipartFile img);

	/**
	 * Lưu thông tin quản trị viên
	 */
	Customer saveAdmin(Customer customer);

	/**
	 * Kiểm tra email khách hàng có tồn tại không
	 */
	Boolean existsEmail(String email);
}
