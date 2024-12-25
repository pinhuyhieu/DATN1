package com.ecom.dto.customer;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private Integer id;
    private String name;
    private String email;
    private String mobileNumber;
}
