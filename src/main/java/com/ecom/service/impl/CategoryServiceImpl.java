package com.ecom.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepository;
import com.ecom.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	/**
	 * Thêm mới hoặc cập nhật danh mục
	 */
	@Override
	public Category saveCategory(Category category) {
		// Kiểm tra xem tên danh mục đã tồn tại chưa
		if (categoryRepository.existsByName(category.getName())) {
			throw new RuntimeException("Danh mục với tên '" + category.getName() + "' đã tồn tại.");
		}

		return categoryRepository.save(category);
	}

	/**
	 * Kiểm tra danh mục có tồn tại theo tên hay không
	 */
	@Override
	public Boolean existCategory(String name) {
		return categoryRepository.existsByName(name);
	}

	/**
	 * Lấy danh sách tất cả danh mục
	 */
	@Override
	public List<Category> getAllCategory() {
		return categoryRepository.findAll();
	}

	/**
	 * Lấy danh sách danh mục đang hoạt động
	 */
	@Override
	public List<Category> getAllActiveCategory() {
		return categoryRepository.findByIsActiveTrue();
	}

	/**
	 * Xóa danh mục theo ID
	 */
	@Override
	public Boolean deleteCategory(int id) {
		Optional<Category> categoryOptional = categoryRepository.findById(id);

		if (categoryOptional.isPresent()) {
			categoryRepository.delete(categoryOptional.get());
			return true;
		}

		return false;
	}

	/**
	 * Lấy danh mục theo ID
	 */
	@Override
	public Optional<Category> getCategoryById(int id) {
		return categoryRepository.findById(id);
	}

	/**
	 * Phân trang danh mục
	 */
	@Override
	public Page<Category> getAllCategorPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return categoryRepository.findAll(pageable);
	}

	/**
	 * Cập nhật danh mục theo ID
	 */
	@Override
	public Category updateCategory(int id, Category category) {
		// Lấy danh mục cũ dựa trên ID
		Category existingCategory = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

		// Cập nhật các trường
		existingCategory.setName(category.getName());
		existingCategory.setImageName(category.getImageName());
		existingCategory.setIsActive(category.getIsActive());

		return categoryRepository.save(existingCategory);
	}
}
