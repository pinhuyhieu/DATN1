package com.ecom.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ecom.model.Customer;
import com.ecom.repository.CustomerRepository;
import com.ecom.service.CustomerService;
import com.ecom.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerService customerService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
										AuthenticationException exception) throws IOException, ServletException {

		String email = request.getParameter("username");

		// Tìm khách hàng bằng email
		Customer customer = customerRepository.findByEmail(email);

		if (customer != null) {
			if (customer.getIsEnable()) { // Kiểm tra tài khoản có được kích hoạt không
				if (customer.getIsEnable()) { // Kiểm tra tài khoản có bị khóa không
					if (customer.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
						// Tăng số lần thất bại
						customerService.increaseFailedAttempt(customer);
					} else {
						// Khóa tài khoản sau khi vượt số lần thất bại tối đa
						customerService.customerAccountLock(customer);
						exception = new LockedException("Your account is locked! Failed attempts: " + AppConstant.ATTEMPT_TIME);
					}
				} else {
					// Nếu tài khoản bị khóa, kiểm tra thời gian hết hạn khóa
					if (customerService.unlockAccountTimeExpired(customer)) {
						exception = new LockedException("Your account is unlocked! Please try to login again.");
					} else {
						exception = new LockedException("Your account is locked! Please try after some time.");
					}
				}
			} else {
				// Tài khoản không hoạt động
				exception = new LockedException("Your account is inactive.");
			}
		} else {
			// Email không tồn tại trong hệ thống
			exception = new LockedException("Invalid email or password.");
		}

		// Chuyển hướng đến trang đăng nhập với thông báo lỗi
		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}
}
