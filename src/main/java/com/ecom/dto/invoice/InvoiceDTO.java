package com.ecom.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private Integer id;
    private String invoiceNumber;
    private Double totalAmount;
    private String paymentMethod;
    private Date createdDate;
}
