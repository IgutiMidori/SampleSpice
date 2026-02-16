package jp.ac.ttc.webapp.bean;

import java.io.Serializable;

public class CartItemBean implements Serializable{
    private int cartItemId;
    private ProductBean product;
    private int productQuantity;

    public CartItemBean() {}

    public int getCartItemId() {
        return cartItemId;
    }
    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }
    public ProductBean getProduct() {
        return product;
    }
    public void setProduct(ProductBean product) {
        this.product = product;
    }
    public int getProductQuantity() {
        return productQuantity;
    }
    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
}
