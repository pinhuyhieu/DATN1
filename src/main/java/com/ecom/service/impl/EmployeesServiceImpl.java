package com.ecom.service.impl;

import com.ecom.model.Employees;
import com.ecom.repository.EmployeesRepository;
import com.ecom.service.EmployeesService;
import com.ecom.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeesServiceImpl implements EmployeesService {

    @Autowired
    private EmployeesRepository employeesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Employees saveEmployee(Employees employee) {
        employee.setRole("ROLE_EMPLOYEE");
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return employeesRepository.save(employee);
    }

    @Override
    public Employees saveAdmin(Employees employee) {
        employee.setRole("ROLE_ADMIN");
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return employeesRepository.save(employee);
    }

    @Override
    public Employees updateEmployee(Employees employee, MultipartFile img) {
        Employees dbEmployee = employeesRepository.findById(employee.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + employee.getId()));

        if (!img.isEmpty()) {
            dbEmployee.setProfileImage(img.getOriginalFilename());
        }

        dbEmployee.setFullName(employee.getFullName());
        dbEmployee.setPhoneNumber(employee.getPhoneNumber());
        dbEmployee.setRole(employee.getRole());
        dbEmployee.setEmail(employee.getEmail());

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

        return employeesRepository.save(dbEmployee);
    }

    @Override
    public Employees getEmployeeByEmail(String email) {
        return employeesRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với email: " + email));
    }

    @Override
    public Employees getEmployeeById(Integer id) {
        return employeesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + id));
    }

    @Override
    public List<Employees> getAllEmployees() {
        return employeesRepository.findAll();
    }

    @Override
    public Boolean existsEmail(String email) {
        return employeesRepository.existsByEmail(email);
    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {
        Employees employee = employeesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + id));
        employee.setIsEnable(status);
        employeesRepository.save(employee);
        return true;
    }

    @Override
    public void increaseFailedAttempt(Employees employee) {
        employee.setFailedAttempt(employee.getFailedAttempt() + 1);
        employeesRepository.save(employee);
    }

    @Override
    public void lockAccount(Employees employee) {
        employee.setAccountNonLocked(false);
        employee.setLockTime(new Timestamp(System.currentTimeMillis()));
        employeesRepository.save(employee);
    }

    @Override
    public boolean unlockAccountTimeExpired(Employees employee) {
        long lockTime = employee.getLockTime().getTime();
        long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;

        if (System.currentTimeMillis() > unlockTime) {
            employee.setAccountNonLocked(true);
            employee.setFailedAttempt(0);
            employee.setLockTime(null);
            employeesRepository.save(employee);
            return true;
        }
        return false;
    }

    @Override
    public void resetFailedAttempt(Integer employeeId) {
        Employees employee = employeesRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + employeeId));
        employee.setFailedAttempt(0);
        employeesRepository.save(employee);
    }
    @Override
    public Optional<Employees> getEmployeesByRole(String role) {
        return employeesRepository.findByRole(role);
    }
    @Override
    public void updateEmployeeResetToken(String email, String resetToken) {
        // Tìm nhân viên dựa trên email
        Employees employee = employeesRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với email: " + email));

        // Cập nhật mã reset token
        employee.setResetToken(resetToken);

        // Lưu lại thay đổi
        employeesRepository.save(employee);
    }
    @Override
    public Employees getEmployeeByToken(String token) {
        return employeesRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token."));
    }
    @Override
    public Employees findByEmail(String email) {
        return employeesRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với email: " + email));
    }
}
