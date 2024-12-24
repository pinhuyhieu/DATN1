package com.ecom.service;

public interface VoucherService {
    /**
     * Kiểm tra và áp dụng mã giảm giá
     * @param voucherCode Mã giảm giá
     * @param totalPrice Tổng giá trị đơn hàng
     * @return Giá trị giảm giá được áp dụng
     */
    Double
    applyVoucher(String voucherCode, Double totalPrice);
}
