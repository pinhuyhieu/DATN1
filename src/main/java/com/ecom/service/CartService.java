import com.ecom.model.Cart;

import java.util.List;

public interface CartService {

	Cart saveCart(Integer productId, Integer customerId);

	List<Cart> getCartsByCustomer(Integer customerId);

	Integer getCountCart(Integer customerId);

	void updateQuantity(String action, Integer cartId);

	void removeCart(Integer cartId);
}
