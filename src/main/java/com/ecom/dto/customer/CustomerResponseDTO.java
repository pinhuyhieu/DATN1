package com.ecom.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private String mobileNumber;
    private Boolean isActive;
}
