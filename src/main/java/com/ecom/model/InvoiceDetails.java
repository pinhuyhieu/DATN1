package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "invoice_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoices invoice; // Liên kết với bảng Invoices

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_details_id", nullable = false)
    private ProductDetails productDetails; // Liên kết với bảng ProductDetails

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
