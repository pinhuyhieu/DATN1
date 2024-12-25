package com.ecom.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.ecom.model.Category;

public interface CategoryService {

	/**
	 * Thêm mới hoặc cập nhật danh mục
	 */
	Category saveCategory(Category category);

	/**
	 * Kiểm tra danh mục có tồn tại theo tên hay không
	 */
	Boolean existCategory(String name);

	/**
	 * Lấy danh sách tất cả danh mục
	 */
	List<Category> getAllCategory();

	/**
	 * Lấy danh sách danh mục đang hoạt động
	 */
	List<Category> getAllActiveCategory();

	/**
	 * Xóa danh mục theo ID
	 */
	Boolean deleteCategory(int id);

	/**
	 * Lấy danh mục theo ID
	 */
	Optional<Category> getCategoryById(int id);

	/**
	 * Phân trang danh mục
	 */
	Page<Category> getAllCategorPagination(Integer pageNo, Integer pageSize);

	/**
	 * Cập nhật danh mục
	 */
	Category updateCategory(int id, Category category);
}
