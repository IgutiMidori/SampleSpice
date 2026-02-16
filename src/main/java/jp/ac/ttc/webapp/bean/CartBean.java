package jp.ac.ttc.webapp.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartBean implements Serializable {
    private int cartId;
    private int subtotal;
    private List<CartItemBean> cartItems = new ArrayList<>();

    public CartBean() {}

    public int getCartId() {
        return cartId;
    }
    public void setCartId(int cartId) {
        this.cartId = cartId;
    }
    public int getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }
    public List<CartItemBean> getCartItems() {
        return cartItems;
    }
    public void setCartItems(List<CartItemBean> cartItems) {
        this.cartItems = cartItems;
    }
    public void addCartItem(CartItemBean cartItem){
        cartItems.add(cartItem);
    }
    public void removeCartItem(int key){
        cartItems.remove(key);
    }
}
