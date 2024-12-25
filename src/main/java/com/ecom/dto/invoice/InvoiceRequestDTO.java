package com.ecom.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequestDTO {
    private Integer customerId;
    private String paymentMethod;
    private String shippingAddress;
}
