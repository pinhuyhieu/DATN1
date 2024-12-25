package com.ecom.service.impl;

import com.ecom.model.Color;
import com.ecom.repository.ColorRepository;
import com.ecom.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorServiceImpl implements ColorService {

    @Autowired
    private ColorRepository colorRepository;

    @Override
    public Color saveColor(Color color) {
        return colorRepository.save(color);
    }

    @Override
    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    @Override
    public Color getColorById(Integer id) {
        return colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found with ID: " + id));
    }

    @Override
    public boolean deleteColor(Integer id) {
        if (colorRepository.existsById(id)) {
            colorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
