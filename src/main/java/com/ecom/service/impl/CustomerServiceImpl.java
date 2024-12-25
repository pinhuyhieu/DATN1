package com.ecom.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Customer;
import com.ecom.repository.CustomerRepository;
import com.ecom.service.CustomerService;
import com.ecom.util.AppConstant;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Customer saveCustomer(Customer customer) {
		customer.setRole("ROLE_CUSTOMER");
		customer.setIsEnable(true);
		customer.setAccountNonLocked(true);
		customer.setFailedAttempt(0);

		String encodePassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodePassword);

		return customerRepository.save(customer);
	}

	@Override
	public Customer getCustomerByEmail(String email) {
		return customerRepository.findByEmail(email);
	}

	@Override
	public List<Customer> getCustomers(String role) {
		return customerRepository.findByRole(role);
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		Optional<Customer> customerOptional = customerRepository.findById(id);

		if (customerOptional.isPresent()) {
			Customer customer = customerOptional.get();
			customer.setIsEnable(status);
			customerRepository.save(customer);
			return true;
		}

		return false;
	}

	@Override
	public void increaseFailedAttempt(Customer customer) {
		int attempt = customer.getFailedAttempt() + 1;
		customer.setFailedAttempt(attempt);
		customerRepository.save(customer);
	}

	@Override
	public void customerAccountLock(Customer customer) {
		customer.setAccountNonLocked(false);
		customer.setLockTime(new Date());
		customerRepository.save(customer);
	}

	@Override
	public boolean unlockAccountTimeExpired(Customer customer) {
		long lockTime = customer.getLockTime().getTime();
		long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		long currentTime = System.currentTimeMillis();

		if (unlockTime < currentTime) {
			customer.setAccountNonLocked(true);
			customer.setFailedAttempt(0);
			customer.setLockTime(null);
			customerRepository.save(customer);
			return true;
		}

		return false;
	}

	@Override
	public void resetAttempt(int customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
		customer.setFailedAttempt(0);
		customerRepository.save(customer);
	}

	@Override
	public void updateCustomerResetToken(String email, String resetToken) {
		Customer customer = customerRepository.findByEmail(email);
		if (customer != null) {
			customer.setResetToken(resetToken);
			customerRepository.save(customer);
		}
	}

	@Override
	public Customer getCustomerByToken(String token) {
		return customerRepository.findByResetToken(token);
	}

	@Override
	public Customer updateCustomer(Customer customer) {
		return customerRepository.save(customer);
	}

	@Override
	public Customer updateCustomerProfile(Customer customer, MultipartFile img) {
		Customer dbCustomer = customerRepository.findById(customer.getId())
				.orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customer.getId()));

		if (!img.isEmpty()) {
			dbCustomer.setProfileImage(img.getOriginalFilename());
		}

		if (!ObjectUtils.isEmpty(dbCustomer)) {
			dbCustomer.setName(customer.getName());
			dbCustomer.setMobileNumber(customer.getMobileNumber());
			dbCustomer.setAddress(customer.getAddress());
			dbCustomer.setCity(customer.getCity());
			dbCustomer.setState(customer.getState());
			dbCustomer.setPincode(customer.getPincode());
			dbCustomer = customerRepository.save(dbCustomer);
		}

		try {
			if (!img.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ img.getOriginalFilename());

				Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dbCustomer;
	}

	@Override
	public Customer saveAdmin(Customer customer) {
		customer.setRole("ROLE_ADMIN");
		customer.setIsEnable(true);
		customer.setAccountNonLocked(true);
		customer.setFailedAttempt(0);

		String encodePassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodePassword);

		return customerRepository.save(customer);
	}

	@Override
	public Boolean existsEmail(String email) {
		return customerRepository.existsByEmail(email);
	}
}
