package com.ecom.repository;

import com.ecom.model.Invoices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoicesRepository extends JpaRepository<Invoices, Integer> {
    List<Invoices> findByCustomerId(Integer customerId);
}
