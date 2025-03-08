package org.example.model.entity;

import org.example.utils.Helper;

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
        this.show = Math.min(show, total) == 0 ? 1 : Math.min(show, total);
        this.total = total;
        this.totalPage = Math.max(total / this.show, 1) + (total % this.show > 0 ? 1 : 0);
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
