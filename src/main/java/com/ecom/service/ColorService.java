package com.ecom.service;

import com.ecom.model.Color;

import java.util.List;

public interface ColorService {
    Color saveColor(Color color);
    List<Color> getAllColors();
    Color getColorById(Integer id);
    boolean deleteColor(Integer id);
}
