package jp.ac.ttc.webapp.bean;

import java.io.Serializable;

public class FavouriteProudctBean implements Serializable{
    private int favouriteProductId;
    private ProductBean product;
    private String addedAt;

    public FavouriteProudctBean() {}
    
    public int getFavouriteProductId() {
        return favouriteProductId;
    }
    public void setFavouriteProductId(int favouriteProductId) {
        this.favouriteProductId = favouriteProductId;
    }
    public ProductBean getProduct() {
        return product;
    }
    public void setProduct(ProductBean product) {
        this.product = product;
    }
    public String getAddedAt() {
        return addedAt;
    }
    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }
}
