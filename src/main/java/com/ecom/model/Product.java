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
@Table(name = "product")
public class Product {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Integer id;

		@Column(name = "title", nullable = false)
		private String title;

		@Column(name = "description")
		private String description;

		@Column(name = "price", nullable = false)
		private Double price;

		@Column(name = "discount_price")
		private Double discountPrice;

		@Column(name = "stock", nullable = false)
		private Integer stock;

		@Column(name = "is_active", nullable = false)
		private Boolean isActive;

		@ManyToOne
		@JoinColumn(name = "category_id", nullable = false)
		private Category category;


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

