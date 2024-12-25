package com.ecom.service.impl;

import com.ecom.model.InvoiceDetails;
import com.ecom.repository.InvoiceDetailsRepository;
import com.ecom.service.InvoiceDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceDetailsServiceImpl implements InvoiceDetailsService {

    @Autowired
    private InvoiceDetailsRepository invoiceDetailsRepository;

    @Override
    public List<InvoiceDetails> getInvoiceDetailsByInvoiceId(Integer invoiceId) {
        return invoiceDetailsRepository.findByInvoiceId(invoiceId);
    }

    @Override
    public InvoiceDetails saveInvoiceDetail(InvoiceDetails invoiceDetail) {
        return invoiceDetailsRepository.save(invoiceDetail);
    }

    @Override
    public void deleteInvoiceDetail(Integer id) {
        invoiceDetailsRepository.deleteById(id);
    }
}
