package com.ecom.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Integer id;
    private Integer productId;
    private Integer quantity;
    private Boolean isOrdered;
}
