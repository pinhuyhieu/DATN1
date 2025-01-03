package com.ecom.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Integer id;
    private String title;
    private Double price;
    private Double discountPrice;
    private String categoryName;
}
