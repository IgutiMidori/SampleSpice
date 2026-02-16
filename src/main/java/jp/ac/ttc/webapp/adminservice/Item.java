package jp.ac.ttc.webapp.adminservice;

import java.util.List;

/**
 * 楽天商品APIのJsonパース用クラス。商品一つ分のデータ。
 * @author suziki-takumi23
 * @since 2026-01-16
 */
public class Item {
    private String itemCode;
    private String itemCaption;
    private String itemName;
    private int itemPrice;
    private List<ImageUrl> mediumImageUrls;
    private List<ImageUrl> smallImageUrls;

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemCaption() { return itemCaption; }
    public void setItemCaption(String itemCaption) { this.itemCaption = itemCaption; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getItemPrice() { return itemPrice; }
    public void setItemPrice(int itemPrice) { this.itemPrice = itemPrice; }

    public List<ImageUrl> getMediumImageUrls() { return mediumImageUrls; }
    public void setMediumImageUrls(List<ImageUrl> mediumImageUrls) { this.mediumImageUrls = mediumImageUrls; }

    public List<ImageUrl> getSmallImageUrls() { return smallImageUrls; }
    public void setSmallImageUrls(List<ImageUrl> smallImageUrls) { this.smallImageUrls = smallImageUrls; }
}
