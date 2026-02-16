package jp.ac.ttc.webapp.bean;

import java.io.Serializable;

public class AddressBean implements Serializable {
    private int addressId;
    private String receiverName;
    private String receiverNameReading;
    private String phoneNumber;
    private String postalCode;
    private String deliveryAddress;

    // 引数なしコンストラクタ（JavaBeansの基本）
    public AddressBean() {}

    // 全引数コンストラクタ（一括セット用）
    public AddressBean(int addressId, String receiverName, String receiverNameReading, 
        String phoneNumber, String postalCode, String deliveryAddress) {
        this.addressId = addressId;
        this.receiverName = receiverName;
        this.receiverNameReading = receiverNameReading;
        this.phoneNumber = phoneNumber;
        this.postalCode = postalCode;
        this.deliveryAddress = deliveryAddress;
    }

    // --- Getter & Setter ---

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverNameReading() {
        return receiverNameReading;
    }

    public void setReceiverNameReading(String receiverNameReading) {
        this.receiverNameReading = receiverNameReading;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}