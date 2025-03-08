package org.example.model.entity;

import java.util.ArrayList;

public class ProductList {
    private ArrayList<Product> result;
    private int page;
    private int totalPage;
    private int show;
    private int total;

    public ProductList(ArrayList<Product> result, int page, int show, int total) {
        this.result = result;
        this.page = page;
        this.show = show;
        this.total = total;
        this.totalPage = Math.max(total / show, 1);
    }

    public ArrayList<Product> getResult() {
        return result;
    }

    public int getPage() {
        return page;
    }

    public int getShow() {
        return show;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getTotal() {
        return total;
    }
}
