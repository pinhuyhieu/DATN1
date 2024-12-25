package com.ecom.repository;

import com.ecom.model.InvoiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetails, Integer> {
    List<InvoiceDetails> findByInvoiceId(Integer invoiceId);
}
