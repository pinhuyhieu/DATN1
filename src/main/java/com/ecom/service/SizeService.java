package com.ecom.service;

import com.ecom.model.Size;

import java.util.List;

public interface SizeService {
    Size saveSize(Size size);
    List<Size> getAllSizes();
    Size getSizeById(Integer id);
    boolean deleteSize(Integer id);
}
