package jp.ac.ttc.webapp.bean;

import java.io.Serializable;
import java.util.List;

public class OrderRecordBean implements Serializable{
    private int orderRecordId;
    private int totalAmount;
    private AddressBean address;
    private String orderedAt;
    private List<OrderItemBean> orderItems;
    private int orderItemCount;
    private boolean orderFlag;
    
    public OrderRecordBean() {}
    
    public void setOrderRecordId(int orderRecordId) {
        this.orderRecordId = orderRecordId;
    }
    public int getOrderRecordId() {
        return orderRecordId;
    }
    public int getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
    public AddressBean getAddress() {
        return address;
    }
    public void setAddress(AddressBean address) {
        this.address = address;
    }
    public String getOrderedAt(){
        return orderedAt;
    }
    public void setOrderedAt(String orderedAt){
        this.orderedAt = orderedAt;
    }
    public List<OrderItemBean> getOrderItems() {
        return orderItems;
    }
    public void setOrderItems(List<OrderItemBean> orderItems) {
        this.orderItems = orderItems;
    }
    public int getOrderItemCount(){
        return orderItemCount;
    }
    public void setOrderItemCount(int orderItemCount){
        this.orderItemCount = orderItemCount;
    }
    public boolean getOrderFlag() {
        return orderFlag;
    }
    public void setOrderFlag(boolean orderFlag) {
        this.orderFlag = orderFlag;
    }
}
