package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ecom.model.*;
import com.ecom.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.util.CommonUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {


	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderStatusService orderStatusService;

	@Autowired
	private EmployeesService employeesService;
	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

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


	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "admin/add_product";
	}

	@GetMapping("/category")
	public String category(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		// m.addAttribute("categorys", categoryService.getAllCategory());
		Page<Category> page = categoryService.getAllCategorPagination(pageNo, pageSize);
		List<Category> categorys = page.getContent();
		m.addAttribute("categorys", categorys);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/category";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category,
							   @RequestParam("file") MultipartFile file,
							   HttpSession session) {

		try {
			// Kiểm tra và xử lý tên tệp hình ảnh
			String imageName = "default.jpg";
			if (file != null && !file.isEmpty()) {
				String originalFileName = file.getOriginalFilename();
				if (!isValidImageFile(originalFileName)) {
					session.setAttribute("errorMsg", "Chỉ chấp nhận tệp hình ảnh (JPG, JPEG, PNG)");
					return "redirect:/admin/category";
				}
				imageName = UUID.randomUUID().toString() + "_" + originalFileName;
			}

			category.setImageName(imageName);

			// Kiểm tra nếu tên danh mục đã tồn tại
			if (categoryService.existCategory(category.getName())) {
				session.setAttribute("errorMsg", "Tên danh mục đã tồn tại");
				return "redirect:/admin/category";
			}

			// Lưu danh mục
			Category savedCategory = categoryService.saveCategory(category);
			if (ObjectUtils.isEmpty(savedCategory)) {
				session.setAttribute("errorMsg", "Không thể lưu! Lỗi máy chủ");
				return "redirect:/admin/category";
			}

			// Lưu tệp hình ảnh nếu có
			if (!"default.jpg".equals(imageName)) {
				saveImageFile(file, imageName);
			}

			session.setAttribute("succMsg", "Lưu danh mục thành công");
			return "redirect:/admin/category";

		} catch (IOException e) {
			session.setAttribute("errorMsg", "Lỗi trong quá trình xử lý: " + e.getMessage());
			return "redirect:/admin/category";
		}
	}

	private boolean isValidImageFile(String fileName) {
		String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		return List.of("jpg", "jpeg", "png").contains(fileExtension);
	}

	private void saveImageFile(MultipartFile file, String imageName) throws IOException {
		File saveDir = new ClassPathResource("static/img/category_img").getFile();
		if (!saveDir.exists()) {
			saveDir.mkdirs(); // Tạo thư mục nếu chưa tồn tại
		}
		Path path = Paths.get(saveDir.getAbsolutePath(), imageName);
		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);

		if (deleteCategory) {
			session.setAttribute("succMsg", "category delete success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
								 HttpSession session) throws IOException {

		// Tìm kiếm danh mục cũ theo ID
		Optional<Category> optionalCategory = categoryService.getCategoryById(category.getId());

		if (!optionalCategory.isPresent()) {
			session.setAttribute("errorMsg", "Category not found with ID: " + category.getId());
			return "redirect:/admin/loadEditCategory/" + category.getId();
		}

		// Lấy thông tin danh mục cũ
		Category oldCategory = optionalCategory.get();

		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

		// Cập nhật thông tin danh mục
		oldCategory.setName(category.getName());
		oldCategory.setIsActive(category.getIsActive());
		oldCategory.setImageName(imageName);

		// Lưu danh mục đã cập nhật
		Category updatedCategory = categoryService.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updatedCategory)) {
			// Lưu tệp hình ảnh nếu có
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "Category update success");
		} else {
			session.setAttribute("errorMsg", "Something went wrong on server");
		}

		return "redirect:/admin/loadEditCategory/" + category.getId();
	}


	@PostMapping("/saveProduct")
	public String saveProduct(
			@Valid @ModelAttribute Product product,
			BindingResult result,
			@RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		if (result.hasErrors()) {
			session.setAttribute("errorMsg", "Dữ liệu không hợp lệ: " + result.getFieldError("stock").getDefaultMessage());
			return "redirect:/admin/loadAddProduct";
		}

		// Nếu không có hình ảnh, sử dụng mặc định
		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
		product.setImage(imageName);

		// Tính toán giá sau giảm giá
		product.setDiscountPrice(product.getPrice() - (product.getPrice() * product.getDiscount() / 100));

		// Gọi phương thức saveProduct với cả đối tượng Product và MultipartFile
		Product savedProduct = productService.saveProduct(product, image);

		if (savedProduct != null) {
			if (!image.isEmpty()) {
				// Lưu tệp hình ảnh
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + imageName);
				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "Lưu sản phẩm thành công!");
		} else {
			session.setAttribute("errorMsg", "Có lỗi xảy ra trong quá trình lưu sản phẩm.");
		}

		return "redirect:/admin/loadAddProduct";
	}



	@GetMapping("/products")
	public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch,
								  @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
								  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		Page<Product> page;

		// Lựa chọn phương thức tìm kiếm hoặc lấy tất cả sản phẩm
		if (ch != null && !ch.isEmpty()) {
			page = productService.searchProductPagination(pageNo, pageSize, ch);
		} else {
			page = productService.getAllProductsPagination(pageNo, pageSize);
		}

		// Định dạng giá tiền cho từng sản phẩm
		page.getContent().forEach(product -> {
			product.setFormattedPrice(productService.formatCurrency(product.getPrice()));
			product.setFormattedDiscountPrice(productService.formatCurrency(product.getDiscountPrice()));

		});

		// Thêm dữ liệu vào Model
		m.addAttribute("products", page.getContent());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/products";
	}


	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Product delete success");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}
		return "redirect:/admin/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "invalid Discount");
		} else {
			Product updateProduct = productService.updateProduct(product.getId(),product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Product update success");
			} else {
				session.setAttribute("errorMsg", "Something wrong on server");
			}
		}
		return "redirect:/admin/editProduct/" + product.getId();
	}

	@GetMapping("/users")
	public String getAllUsers(Model m, @RequestParam Integer type) {
		List<Customer> users = null;
		if (type == 1) {
			users = customerService.getCustomers("ROLE_USER");
		} else {
			users = customerService.getCustomers("ROLE_ADMIN");
		}
		m.addAttribute("userType",type);
		m.addAttribute("users", users);
		return "/admin/users";
	}

	@GetMapping("/updateSts")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id,@RequestParam Integer type, HttpSession session) {
		Boolean f = customerService.updateAccountStatus(id, status);
		if (f) {
			session.setAttribute("succMsg", "Account Status Updated");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}
		return "redirect:/admin/users?type="+type;
	}

	@GetMapping("/orders")
	public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
//		List<ProductOrder> allOrders = orderService.getAllOrders();
//		m.addAttribute("orders", allOrders);
//		m.addAttribute("srch", false);

		Page<Orders> page = orderService.getAllOrdersPagination(pageNo, pageSize);


		m.addAttribute("orders", page.getContent());
		m.addAttribute("srch", false);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "/admin/orders";
	}

	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer statusId, HttpSession session) {

		// Lấy trạng thái đơn hàng từ Service
		Optional<OrderStatus> optionalStatus = orderStatusService.getOrderStatusById(statusId);
		if (!optionalStatus.isPresent()) {
			session.setAttribute("errorMsg", "Invalid order status ID.");
			return "redirect:/admin/orders";
		}

		OrderStatus orderStatus = optionalStatus.get();

		// Cập nhật trạng thái đơn hàng
		Orders updatedOrder = orderService.updateOrderStatus(id, statusId);

		// Gửi email thông báo (nếu cần)
		try {
			commonUtil.sendMailForOrder(updatedOrder, orderStatus.getStatusName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Thông báo kết quả
		if (!ObjectUtils.isEmpty(updatedOrder)) {
			session.setAttribute("succMsg", "Order status updated successfully.");
		} else {
			session.setAttribute("errorMsg", "Failed to update order status.");
		}

		return "redirect:/admin/orders";
	}

	@GetMapping("/search-order")
	public String searchProduct(@RequestParam String orderId, Model m, HttpSession session,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		if (orderId != null && orderId.length() > 0) {

			Orders order = orderService.getOrdersByOrderId(orderId.trim());

			if (ObjectUtils.isEmpty(order)) {
				session.setAttribute("errorMsg", "Incorrect orderId");
				m.addAttribute("orderDtls", null);
			} else {
				m.addAttribute("orderDtls", order);
			}

			m.addAttribute("srch", true);
		} else {
//			List<ProductOrder> allOrders = orderService.getAllOrders();
//			m.addAttribute("orders", allOrders);
//			m.addAttribute("srch", false);

			Page<Orders> page = orderService.getAllOrdersPagination(pageNo, pageSize);
			m.addAttribute("orders", page);
			m.addAttribute("srch", false);

			m.addAttribute("pageNo", page.getNumber());
			m.addAttribute("pageSize", pageSize);
			m.addAttribute("totalElements", page.getTotalElements());
			m.addAttribute("totalPages", page.getTotalPages());
			m.addAttribute("isFirst", page.isFirst());
			m.addAttribute("isLast", page.isLast());

		}
		return "/admin/orders";

	}

	@GetMapping("/add-admin")
	public String loadAdminAdd() {
		return "/admin/add_admin";
	}

	@PostMapping("/save-admin")
	public String saveAdmin(
			@ModelAttribute Employees employee,
			@RequestParam("img") MultipartFile file,
			HttpSession session) throws IOException {

		// Xử lý ảnh hồ sơ (nếu có)
		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();

		employee.setRole("ROLE_ADMIN");
		// Lưu nhân viên
		Employees savedEmployee = employeesService.saveEmployee(employee);

		if (savedEmployee != null) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + imageName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "Admin registered successfully.");
		} else {
			session.setAttribute("errorMsg", "Error occurred while registering admin.");
		}
		return "redirect:/admin/add-admin";
	}

	@GetMapping("/profile")
	public String profile() {
		return "/admin/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(
			@ModelAttribute Employees employee,
			@RequestParam("img") MultipartFile file,
			HttpSession session) throws IOException {

		// Lấy thông tin nhân viên hiện tại từ cơ sở dữ liệu
		Employees existingEmployee = employeesService.getEmployeeById(employee.getId());

		// Cập nhật thông tin cơ bản
		existingEmployee.setFullName(employee.getFullName());
		existingEmployee.setPhoneNumber(employee.getPhoneNumber());

		// Nếu có file ảnh được tải lên, xử lý cập nhật ảnh
		if (!file.isEmpty()) {
			String imageName = file.getOriginalFilename();
			existingEmployee.setProfileImage(imageName); // Gán tên ảnh vào thuộc tính profileImage

			// Lưu file ảnh vào thư mục
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + imageName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}

		// Lưu nhân viên cập nhật vào cơ sở dữ liệu
		Employees updatedEmployee = employeesService.updateEmployee(employee,file);

		if (updatedEmployee != null) {
			session.setAttribute("succMsg", "Cập nhật thông tin thành công.");
		} else {
			session.setAttribute("errorMsg", "Không thể cập nhật thông tin.");
		}
		return "redirect:/admin/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(
			@RequestParam String newPassword,
			@RequestParam String currentPassword,
			Principal principal,
			HttpSession session) {

		// Lấy thông tin nhân viên đăng nhập từ email
		Employees loggedInEmployee = employeesService.findByEmail(principal.getName());

		// Kiểm tra mật khẩu hiện tại
		if (passwordEncoder.matches(currentPassword, loggedInEmployee.getPassword())) {
			// Mã hóa và cập nhật mật khẩu mới
			loggedInEmployee.setPassword(passwordEncoder.encode(newPassword));
			employeesService.saveEmployee(loggedInEmployee);
			session.setAttribute("succMsg", "Đổi mật khẩu thành công.");
		} else {
			session.setAttribute("errorMsg", "Mật khẩu hiện tại không chính xác.");
		}
		return "redirect:/admin/profile";
	}


}
