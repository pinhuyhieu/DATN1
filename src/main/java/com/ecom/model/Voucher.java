package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code; // Mã giảm giá
    private Double discountValue; // Giá trị giảm giá
    private String discountType; // "fixed" hoặc "percentage"
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}
