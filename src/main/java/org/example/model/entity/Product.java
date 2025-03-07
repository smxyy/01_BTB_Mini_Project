package org.example.model.entity;

import java.sql.Date;

public class Product {
    private int id;
    private String name;
    private double unitPrice;
    private int quantity;
    private Date impotedDate;

    public Product(int id, String name, double unitPrice, int quantity, Date impotedDate) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.impotedDate = impotedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getImpotedDate() {
        return impotedDate;
    }

    public void setImpotedDate(Date impotedDate) {
        this.impotedDate = impotedDate;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                ", impotedDate=" + impotedDate +
                '}';
    }
}
