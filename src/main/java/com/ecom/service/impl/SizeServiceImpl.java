package com.ecom.service.impl;

import com.ecom.model.Size;
import com.ecom.repository.SizeRepository;
import com.ecom.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SizeServiceImpl implements SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    @Override
    public Size saveSize(Size size) {
        return sizeRepository.save(size);
    }

    @Override
    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    @Override
    public Size getSizeById(Integer id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found with ID: " + id));
    }

    @Override
    public boolean deleteSize(Integer id) {
        if (sizeRepository.existsById(id)) {
            sizeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
