package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import com.ecom.model.Customer;
import com.ecom.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.util.CommonUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private VoucherService voucherService;


	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal principal, Model model) {
		if (principal != null) {
			// Lấy email từ đối tượng Principal
			String email = principal.getName();

			// Lấy thông tin khách hàng dựa trên email
			Customer customer = customerService.getCustomerByEmail(email);
			if (customer != null) {
				model.addAttribute("user", customer);

				// Đếm số lượng sản phẩm trong giỏ hàng
				Integer cartItemCount = cartService.getCountCart(customer.getId());
				model.addAttribute("countCart", cartItemCount);
			}
		}

		// Lấy danh sách tất cả các danh mục đang hoạt động
		List<Category> activeCategories = categoryService.getAllActiveCategory();
		model.addAttribute("categories", activeCategories);
	}
	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed");
		} else {
			session.setAttribute("succMsg", "Product added to cart");
		}
		return "redirect:/product/" + pid;
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {

		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/cart";
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}

	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = customerService.getUserByEmail(email);
		return userDtls;
	}

	@GetMapping("/orders")
	public String orderPage(Principal p, Model m, @RequestParam(required = false) String voucherCode) {
		UserDtls user = getLoggedInUserDetails(p);

		// Lấy giỏ hàng của người dùng
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);

		if (carts.size() > 0) {
			// Tính tổng giá trị đơn hàng
			Double orderPrice = carts.stream()
					.mapToDouble(cart -> cart.getProduct().getDiscountPrice() * cart.getQuantity())
					.sum();

			// Áp dụng phí vận chuyển và thuế
			Double deliveryFee = 250.0;
			Double tax = 100.0;
			Double totalOrderPrice = orderPrice + deliveryFee + tax;

			// Kiểm tra mã giảm giá nếu có
			Double discount = 0.0;
			if (voucherCode != null && !voucherCode.isEmpty()) {
				try {
					discount = voucherService.applyVoucher(voucherCode, totalOrderPrice);
					m.addAttribute("voucherCode", voucherCode);
					m.addAttribute("discount", discount);
				} catch (RuntimeException e) {
					m.addAttribute("errorMsg", e.getMessage());
				}
			}

			// Tổng sau khi áp dụng mã giảm giá
			Double totalAfterDiscount = totalOrderPrice - discount;

			// Gửi dữ liệu sang giao diện
			m.addAttribute("orderPrice", orderPrice);
			m.addAttribute("totalOrderPrice", totalOrderPrice);
			m.addAttribute("totalAfterDiscount", totalAfterDiscount);
		}

		return "/user/order";
	}




	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p, HttpSession session) {
		UserDtls user = getLoggedInUserDetails(p);
		try {
			orderService.saveOrder(user.getId(), request);
			return "redirect:/user/success";
		} catch (RuntimeException e) {
			session.setAttribute("errorMsg", e.getMessage());
			return "redirect:/user/cart";
		} catch (Exception e) {
			session.setAttribute("errorMsg", "Đã xảy ra lỗi trong quá trình đặt hàng.");
			return "redirect:/user/cart";
		}
	}


	@GetMapping("/success")
	public String loadSuccess() {
		return "/user/success";
	}

	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
		UserDtls loginUser = getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
		m.addAttribute("orders", orders);
		return "/user/my_orders";
	}

	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
		
		try {
			commonUtil.sendMailForProductOrder(updateOrder, status);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Status Updated");
		} else {
			session.setAttribute("errorMsg", "status not updated");
		}
		return "redirect:/user/user-orders";
	}

	@GetMapping("/profile")
	public String profile() {
		return "/user/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = customerService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/user/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDtls loggedInUserDetails = getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser = customerService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "Password not updated !! Error in server");
			} else {
				session.setAttribute("succMsg", "Password Updated sucessfully");
			}
		} else {
			session.setAttribute("errorMsg", "Current Password incorrect");
		}

		return "redirect:/user/profile";
	}

}
