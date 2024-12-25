package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "employees")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    // Thêm các trường bổ sung
    @Column(name = "profile_image", length = 255)
    private String profileImage; // Ảnh đại diện

    @Column(name = "address", length = 255)
    private String address; // Địa chỉ nhân viên

    @Column(name = "city", length = 100)
    private String city; // Thành phố

    @Column(name = "state", length = 100)
    private String state; // Tỉnh/thành phố

    @Column(name = "pincode", length = 10)
    private String pincode; // Mã bưu điện

    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable = true; // Tài khoản được kích hoạt hay không

    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked = true; // Tài khoản có bị khóa không

    @Column(name = "failed_attempt", nullable = false)
    private Integer failedAttempt = 0; // Số lần đăng nhập thất bại

    @Column(name = "lock_time")
    private Timestamp lockTime; // Thời gian khóa tài khoản

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt; // Thời gian tạo nhân viên

    @Column(name = "updated_at")
    private Timestamp updatedAt; // Thời gian cập nhật thông tin
    @Column(name = "reset_token")
    private String resetToken;
}
