package jp.ac.ttc.webapp.bean;

/**
 * @author koyama
 * @version 1.0
 * @created 18-12-2025 11:10:17
 */
public class ProductBean implements java.io.Serializable {

    private String apiItemId;
    private int productId;
    private String productName;
    private int price;
    private int capacity;
    private String originCountry;
    private String imageUrl;
    private String productDescription;
    private int spiceId;
    private int stockQuantity;
    private int salesVolume;
    private boolean favoriteFlag;
    private int favoriteUserCount;
    private boolean activeFlag;
    private String updatedAt;
    private SpiceBean spice;

    public ProductBean() {
    }

    // Getters
    public String getApiItemId() {
        return apiItemId;
    }

    public int getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public int getPrice() {
        return price;
    }
    public int getCapacity() {
        return capacity;
    }
    public String getOriginCountry() {
        return originCountry;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getProductDescription() {
        return productDescription;
    }
    public int getSpiceId() {
        return spiceId;
    }
    public int getStockQuantity() {
        return stockQuantity;
    }
    public int getSalesVolume() {
        return salesVolume;
    }
    public boolean isFavoriteFlag() {
        return favoriteFlag;
    }
    public int getFavoriteUserCount() {
        return favoriteUserCount;
    }
    public boolean getActiveFlag(){
        return activeFlag;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public SpiceBean getSpice(){
        return spice;
    }

    // Setters
    public void setApiItemId(String apiItemId) {
        this.apiItemId = apiItemId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    public void setSpiceId(int spiceId) {
        this.spiceId = spiceId;
    }
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    public void setSalesVolume(int salesVolume) {
        this.salesVolume = salesVolume;
    }
    public void setFavoriteFlag(boolean favoriteFlag) {
        this.favoriteFlag = favoriteFlag;
    }
    public void setFavoriteUserCount(int favoriteUserCount) {
        this.favoriteUserCount = favoriteUserCount;
    }
    public void setActiveFlag(boolean activeFlag){
        this.activeFlag = activeFlag;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setSpice(SpiceBean spice){
        this.spice = spice;
    }
}