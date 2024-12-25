package com.ecom.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Employees;

public interface EmployeesService {
    Employees saveEmployee(Employees employee);

    Employees getEmployeeByEmail(String email);
    Optional<Employees> getEmployeesByRole(String role);
    Boolean updateAccountStatus(Integer id, Boolean status);
    void increaseFailedAttempt(Employees employee);
    void lockAccount(Employees employee);
    boolean unlockAccountTimeExpired(Employees employee);
    void resetFailedAttempt(Integer employeeId);

    void updateEmployeeResetToken(String email, String resetToken);
    Employees saveAdmin(Employees employee);
    Employees updateEmployee(Employees employee, MultipartFile img);
    Boolean existsEmail(String email);
    Employees getEmployeeById(Integer id);
    List<Employees> getAllEmployees();
    Employees getEmployeeByToken(String token);
    Employees findByEmail(String email);



}
