package com.ecom.config;

import com.ecom.model.Customer;
import com.ecom.model.Employees;
import com.ecom.repository.CustomerRepository;
import com.ecom.repository.EmployeesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private EmployeesRepository employeeRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Attempt to find the user in the Customers table
		Customer customer = customerRepository.findByEmail(username);
		if (customer != null) {
			return CustomUser.fromCustomer(customer);
		}

		// If not found in Customers, check the Employees table
		Employees employee = employeeRepository.findByEmail(username)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với email: " + username));

		// If user is not found in both tables, throw an exception
		throw new UsernameNotFoundException("User not found with email: " + username);
	}
}
