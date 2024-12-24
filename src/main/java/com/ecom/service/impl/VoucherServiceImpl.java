package com.ecom.service.impl;

import com.ecom.model.Voucher;
import com.ecom.repository.VoucherRepository;
import com.ecom.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Override
    public Double applyVoucher(String voucherCode, Double totalPrice) {
        // Tìm voucher trong cơ sở dữ liệu
        Voucher voucher = voucherRepository.findByCode(voucherCode);

        // Kiểm tra nếu voucher không tồn tại hoặc không hoạt động
        if (voucher == null || !voucher.getIsActive()) {
            throw new RuntimeException("Voucher không hợp lệ hoặc đã hết hạn.");
        }

        // Kiểm tra thời gian hiệu lực của voucher
        LocalDate today = LocalDate.now();
        if (today.isBefore(voucher.getStartDate()) || today.isAfter(voucher.getEndDate())) {
            throw new RuntimeException("Voucher đã hết hạn.");
        }

        // Tính toán giá trị giảm giá dựa trên loại giảm giá
        if ("percentage".equalsIgnoreCase(voucher.getDiscountType())) {
            return totalPrice * voucher.getDiscountValue() / 100; // Giảm theo phần trăm
        } else if ("fixed".equalsIgnoreCase(voucher.getDiscountType())) {
            return Math.min(voucher.getDiscountValue(), totalPrice); // Giảm giá cố định
        } else {
            throw new RuntimeException("Loại giảm giá không hợp lệ.");
        }
    }
}
