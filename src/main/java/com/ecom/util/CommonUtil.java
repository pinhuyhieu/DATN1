package com.ecom.util;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

import com.ecom.model.Customer;
import com.ecom.model.InvoiceDetails;
import com.ecom.model.Orders;
import com.ecom.model.ProductDetails;
import com.ecom.repository.InvoiceDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ecom.service.CustomerService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private CustomerService customerService;
	@Autowired
	private InvoiceDetailsRepository invoiceDetailsRepository;

	public Boolean sendMail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("nguyenhuyhieu2004@gmail.com", "Shooping Cart");
		helper.setTo(reciepentEmail);

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
				+ "\">Change my password</a></p>";
		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {

		// http://localhost:8080/forgot-password
		String siteUrl = request.getRequestURL().toString();

		return siteUrl.replace(request.getServletPath(), "");
	}
	
	String msg=null;;

	public Boolean sendMailForOrder(Orders order, String status) throws Exception {
		String msg = "<p>Hello [[name]],</p>"
				+ "<p>Thank you for your order. Your order is currently <b>[[orderStatus]]</b>.</p>"
				+ "<p><b>Order Details:</b></p>"
				+ "<p>Order ID : [[orderId]]</p>"
				+ "<p>Shipping Method : [[shippingMethod]]</p>"
				+ "<p>Total Amount : [[totalAmount]]</p>"
				+ "<p>Payment Type : [[paymentType]]</p>"
				+ "<p><b>Product Details:</b></p>"
				+ "<ul>[[productDetails]]</ul>";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("nguyenhuyhieu2004@gmail.com", "Shopping Cart");
		helper.setTo(order.getCustomer().getEmail());

		StringBuilder productDetails = new StringBuilder();

		// Truy vấn InvoiceDetails dựa trên invoiceId từ order
		List<InvoiceDetails> details = invoiceDetailsRepository.findByInvoiceId(order.getInvoice().getId());

		for (InvoiceDetails detail : details) {
			ProductDetails productDetailsEntity = detail.getProductDetails();
			productDetails.append("<li>")
					.append(productDetailsEntity.getProduct().getTitle()) // Tên sản phẩm
					.append(" (Color: ").append(productDetailsEntity.getColor().getColorName()).append(", ") // Màu
					.append("Size: ").append(productDetailsEntity.getSize().getSizeName()).append(", ") // Kích thước
					.append("Quantity: ").append(detail.getQuantity()).append(", ") // Số lượng
					.append("Price: ").append(detail.getQuantity() * productDetailsEntity.getProduct().getDiscountPrice()) // Tổng giá
					.append(")")
					.append("</li>");
		}

		msg = msg.replace("[[name]]", order.getCustomer().getName());
		msg = msg.replace("[[orderStatus]]", status);
		msg = msg.replace("[[orderId]]", order.getId().toString());
		msg = msg.replace("[[shippingMethod]]", order.getShippingMethod());
		msg = msg.replace("[[totalAmount]]", order.getTotalAmount().toString());
		msg = msg.replace("[[paymentType]]", order.getPaymentMethod());
		msg = msg.replace("[[productDetails]]", productDetails.toString());

		helper.setSubject("Order Status Update");
		helper.setText(msg, true);

		mailSender.send(message);

		return true;
	}



	public Customer getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		Customer userDtls = customerService.getCustomerByEmail(email);
		return userDtls;
	}

	

}
