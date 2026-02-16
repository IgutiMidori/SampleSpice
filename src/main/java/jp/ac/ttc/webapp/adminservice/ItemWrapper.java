package jp.ac.ttc.webapp.adminservice;

/**
 * 楽天商品APIのJsonパース用クラス。商品そのものを格納する
 * @author suzuki-takumi23
 * @since 2026-01-16
 */
public class ItemWrapper {
    private Item Item;

    public Item getItem() { return Item; }
    public void setItem(Item item) { Item = item; }
}
