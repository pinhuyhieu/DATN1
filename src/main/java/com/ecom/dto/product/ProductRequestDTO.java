package com.ecom.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    private String title;
    private String description;
    private Double price;
    private Double discountPrice;
    private Integer categoryId;
}
