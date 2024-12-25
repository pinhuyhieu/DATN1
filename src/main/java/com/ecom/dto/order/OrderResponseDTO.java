package com.ecom.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Integer id;
    private String customerName;
    private Date orderDate;
    private Double totalAmount;
    private String status;
    private List<String> products; // List of product names
}
