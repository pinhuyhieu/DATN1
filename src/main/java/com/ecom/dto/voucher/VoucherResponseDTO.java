package com.ecom.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponseDTO {
    private String code;
    private String discountType;
    private Double discountValue;
    private String validity; // Example: "Valid until 2024-12-31"
}
