package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.Voucher;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    Voucher findByCode(String code);
}
