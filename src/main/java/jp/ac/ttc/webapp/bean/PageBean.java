package jp.ac.ttc.webapp.bean;

import java.io.Serializable;

public class PageBean implements Serializable {
    public static final int SPICE_PAGE_SIZE = 16;
    public static final int PRODUCT_PAGE_SIZE = 20;
    public static final int ORDER_RECODE_PAGE_SIZE = 15;
    public static final int RECIPE_PAGE_SIZE = 5;
    

    private int offset;
    private int limit;
    private int currentPage;
    private boolean next;
    private boolean prev;
    private int maxPage;

    public PageBean(int currentPage, int totalItems, int pageSize) {
        this.currentPage = currentPage;
        this.offset = (currentPage - 1) * pageSize;
        this.maxPage = (totalItems + pageSize - 1) / pageSize; // 最大ページ数
        this.next = currentPage < maxPage;
        this.prev = currentPage > 1;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public boolean getNext() {
        return next;
    }

    public boolean getPrev() {
        return prev;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
