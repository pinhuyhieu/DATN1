package com.ecom.config;

import com.ecom.model.Customer;
import com.ecom.model.Employees;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public class CustomUser implements UserDetails {

	private String email;
	private String password;
	private String role;
	private boolean isEnabled;
	private boolean isAccountNonLocked;

	public CustomUser(String email, String password, String role, boolean isEnabled, boolean isAccountNonLocked) {
		this.email = email;
		this.password = password;
		this.role = role;
		this.isEnabled = isEnabled;
		this.isAccountNonLocked = isAccountNonLocked;
	}

	// Factory method for creating CustomUser from Customer entity
	public static CustomUser fromCustomer(Customer customer) {
		return new CustomUser(
				customer.getEmail(),
				customer.getPassword(),
				customer.getRole(),
				customer.getIsEnable(),
				customer.getAccountNonLocked()
		);
	}

	// Factory method for creating CustomUser from Employees entity
	public static CustomUser fromEmployee(Employees employee) {
		return new CustomUser(
				employee.getEmail(),
				employee.getPassword(),
				employee.getRole(),
				true,  // Assuming all employees are enabled
				true   // Assuming employee accounts are not locked
		);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(new SimpleGrantedAuthority(this.role));
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.isAccountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}
}
