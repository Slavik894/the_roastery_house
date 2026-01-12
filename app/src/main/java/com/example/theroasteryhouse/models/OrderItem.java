package com.example.theroasteryhouse.models;

public class OrderItem {
    private String name;
    private String size;
    private double price;

    private boolean isHeader;

    public OrderItem(String name, String size, double price) {
        this.name = name;
        this.size = size;
        this.price = price;
        this.isHeader = false;
    }
    public OrderItem(String headerTitle) {
        this.name = headerTitle;
        this.size = "";
        this.price = 0.0;
        this.isHeader = true;
    }

    public String getName() { return name; }
    public String getSize() { return size; }
    public double getPrice() { return price; }
    public boolean isHeader() { return isHeader; }

    public String getDisplayName() {
        if (isHeader) return name;
        if (size == null || size.isEmpty()) return name;
        return name + " (" + size + ")";
    }
}