package com.ecom.service;

import com.ecom.model.InvoiceDetails;

import java.util.List;

public interface InvoiceDetailsService {
    List<InvoiceDetails> getInvoiceDetailsByInvoiceId(Integer invoiceId);

    InvoiceDetails saveInvoiceDetail(InvoiceDetails invoiceDetail);

    void deleteInvoiceDetail(Integer id);
}
