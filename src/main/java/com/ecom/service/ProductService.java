package com.ecom.service;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;

public interface ProductService {
	public List<Product> getAllProducts();

	public boolean deleteProduct(Integer id);

	Product saveProduct(Product product, MultipartFile image);

	Product updateProduct(Integer id, Product updatedProduct, MultipartFile image);

	public Product getProductById(Integer id);

	public List<Product> getAllActiveProducts(String category);

	public List<Product> searchProduct(String ch);

	public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category);

	public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch);

	public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize);

	public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch);
	public String formatCurrency(double amount);
	}
