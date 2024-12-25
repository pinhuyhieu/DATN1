package com.ecom.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private Integer customerId;
    private Integer addressId;
    private String paymentMethod;
    private String shippingMethod;
}
