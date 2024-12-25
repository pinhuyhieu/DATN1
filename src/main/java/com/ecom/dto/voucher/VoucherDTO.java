package com.ecom.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private Integer id;
    private String code;
    private String discountType;
    private Double discountValue;
    private Boolean isActive;
}
