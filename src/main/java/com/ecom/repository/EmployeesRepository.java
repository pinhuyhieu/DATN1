package com.ecom.repository;

import com.ecom.model.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeesRepository extends JpaRepository<Employees, Integer> {
    public Optional<Employees> findByEmail(String email);

    public Optional<Employees> findByRole(String role);

    public Optional<Employees> findByResetToken(String token);

    public Boolean existsByEmail(String email);


}
