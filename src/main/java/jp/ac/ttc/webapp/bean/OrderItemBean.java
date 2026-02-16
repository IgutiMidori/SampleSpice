package jp.ac.ttc.webapp.bean;

import java.io.Serializable;

public class OrderItemBean implements Serializable{
    private  int orderItemId;
    private ProductBean product;
    private int quantity;
    
    public OrderItemBean() {}
    
    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }
    public int getOrderItemId() {
        return orderItemId;
    }
    public ProductBean getProduct() {
        return product;
    }
    public void setProduct(ProductBean product) {
        this.product = product;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
