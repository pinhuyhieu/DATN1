package com.ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 500)
	private String title;

	@Column(length = 5000)
	private String description;

	private String category;

	private Double price;
	@NotNull(message = "Số lượng không được để trống.")
	@Min(value = 0, message = "Số lượng phải là số nguyên dương.")
	private int stock;

	private String image;

	private int discount;
	
	private Double discountPrice;
	
	private Boolean isActive;
	@Transient
	private String formattedPrice;

	public String getFormattedPrice() {
		return formattedPrice;
	}

	public void setFormattedPrice(String formattedPrice) {
		this.formattedPrice = formattedPrice;
	}
	@Transient
	private String formattedDiscountPrice;
	public String getFormattedDiscountPrice() {
		return formattedDiscountPrice;
	}

	public void setFormattedDiscountPrice(String formattedDiscountPrice) {
		this.formattedDiscountPrice = formattedDiscountPrice;
	}
}
