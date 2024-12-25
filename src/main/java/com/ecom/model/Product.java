package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "category", length = 255)
	private String category; // Tên danh mục, có thể thay thế bằng `Category` entity nếu cần liên kết.

	@Column(name = "description", length = 5000)
	private String description; // Mô tả sản phẩm.

	@Column(name = "discount")
	private Integer discount; // Giá trị giảm giá (phần trăm hoặc cố định).

	@Column(name = "discount_price", precision = 10, scale = 2)
	private Double discountPrice; // Giá sau giảm giá.

	@Column(name = "image", length = 255)
	private String image; // Đường dẫn hình ảnh sản phẩm.

	@Column(name = "is_active")
	private Boolean isActive; // Trạng thái sản phẩm (hoạt động hoặc không).

	@Column(name = "price", precision = 10, scale = 2)
	private Double price; // Giá sản phẩm.

	@Column(name = "stock")
	private Integer stock; // Số lượng tồn kho.

	@Column(name = "title", length = 500)
	private String title; // Tên sản phẩm.

	@Column(name = "category_id")
	private Integer categoryId; // ID danh mục liên kết.

	@Transient
	private String formattedPrice; // Định dạng giá hiển thị.

	public String getFormattedPrice() {
		return formattedPrice;
	}

	public void setFormattedPrice(String formattedPrice) {
		this.formattedPrice = formattedPrice;
	}

	@Transient
	private String formattedDiscountPrice; // Định dạng giá giảm giá hiển thị.

	public String getFormattedDiscountPrice() {
		return formattedDiscountPrice;
	}

	public void setFormattedDiscountPrice(String formattedDiscountPrice) {
		this.formattedDiscountPrice = formattedDiscountPrice;
	}
}
