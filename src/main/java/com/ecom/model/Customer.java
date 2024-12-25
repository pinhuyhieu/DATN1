package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "customers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable;

    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked;

    @Column(name = "failed_attempt")
    private Integer failedAttempt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lock_time")
    private Date lockTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.failedAttempt = (this.failedAttempt == null) ? 0 : this.failedAttempt;
        this.accountNonLocked = (this.accountNonLocked == null) ? true : this.accountNonLocked;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

}
