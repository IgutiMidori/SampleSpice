package jp.ac.ttc.webapp.adminservice;

import java.util.List;

/**
 * 商品データオブジェクトを配列で格納するクラス
 * @author suzuki-takumi23
 * @since 2026-01-16
 */
public class Root {
    private List<ItemWrapper> Items;

    public List<ItemWrapper> getItems() { return Items; }
    public void setItems(List<ItemWrapper> items) { Items = items; }
}
