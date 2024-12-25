package com.ecom.service.impl;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Product saveProduct(Product product, MultipartFile image) {
		// Xử lý ảnh
		if (image != null && !image.isEmpty()) {
			String imageName = saveImage(image);
			product.setImage(imageName);
		}

		// Tính giá sau giảm giá
		calculateDiscountPrice(product);

		return productRepository.save(product);
	}


	@Override
	public Product updateProduct(Integer id, Product updatedProduct, MultipartFile image) {
		Product existingProduct = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

		// Cập nhật thông tin từ updatedProduct
		existingProduct.setTitle(updatedProduct.getTitle());
		existingProduct.setDescription(updatedProduct.getDescription());
		existingProduct.setCategory(updatedProduct.getCategory());
		existingProduct.setPrice(updatedProduct.getPrice());
		existingProduct.setStock(updatedProduct.getStock());
		existingProduct.setIsActive(updatedProduct.getIsActive());
		existingProduct.setDiscount(updatedProduct.getDiscount());

		// Xử lý ảnh
		if (image != null && !image.isEmpty()) {
			String imageName = saveImage(image);
			existingProduct.setImage(imageName);
		}

		// Tính giá sau giảm giá
		calculateDiscountPrice(existingProduct);

		return productRepository.save(existingProduct);
	}


	@Override
	public Product getProductById(Integer id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
		product.setFormattedPrice(formatCurrency(product.getPrice()));
		product.setFormattedDiscountPrice(formatCurrency(product.getDiscountPrice()));
		return product;
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}
	@Override
	public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findAll(pageable);
	}
	@Override
	public boolean deleteProduct(Integer id) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
			return true;
		}
		return false;
	}
	@Override
	public List<Product> searchProduct(String keyword) {
		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword);
	}

	@Override
	public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String keyword) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword, pageable);
	}
	private void calculateDiscountPrice(Product product) {
		double discountAmount = product.getPrice() * (product.getDiscount() / 100.0);
		double discountPrice = product.getPrice() - discountAmount;
		product.setDiscountPrice(discountPrice);
	}
	private String saveImage(MultipartFile image) {
		try {
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			return image.getOriginalFilename();
		} catch (Exception e) {
			throw new RuntimeException("Failed to save image", e);
		}
	}
	@Override
	public List<Product> getAllActiveProducts(String category) {
		if (category == null || category.isEmpty()) {
			// Nếu không có danh mục, trả về tất cả sản phẩm đang hoạt động
			return productRepository.findByIsActiveTrue();
		} else {
			// Nếu có danh mục, tìm sản phẩm đang hoạt động theo danh mục
			return productRepository.findByIsActiveTrueAndCategoryNameIgnoreCase(category);
		}
	}
	@Override
	public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		if (category == null || category.isEmpty()) {
			// Lấy tất cả sản phẩm đang hoạt động nếu không có danh mục
			return productRepository.findByIsActiveTrue(pageable);
		} else {
			// Lấy sản phẩm đang hoạt động theo danh mục cụ thể
			return productRepository.findByIsActiveTrueAndTitleContainingIgnoreCase(category, pageable);
		}
	}
	@Override
	public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String keyword) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		if ((category == null || category.isEmpty()) && (keyword == null || keyword.isEmpty())) {
			// Nếu không có category và keyword, lấy tất cả sản phẩm đang hoạt động
			return productRepository.findByIsActiveTrue(pageable);
		} else if (category != null && !category.isEmpty()) {
			// Nếu chỉ có category, tìm theo category và trạng thái isActive
			return productRepository.findByIsActiveTrueAndCategoryNameIgnoreCaseAndTitleContainingIgnoreCase(category, keyword, pageable);
		} else {
			// Nếu chỉ có keyword, tìm theo keyword và trạng thái isActive
			return productRepository.findByIsActiveTrueAndTitleContainingIgnoreCase(keyword, pageable);
		}
	}
	@Override
	public String formatCurrency(double amount) {
		// Sử dụng định dạng tiền tệ của Việt Nam
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
		return currencyFormatter.format(amount); // Trả về chuỗi định dạng, ví dụ: "90.000 ₫"
	}
}
