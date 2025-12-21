package com.example.theroasteryhouse.models;

public class OrderItem {
    private String name;
    private String size;
    private double price;

    public OrderItem(String name, String size, double price) {
        this.name = name;
        this.size = size;
        this.price = price;
    }

    public String getName() { return name; }
    public String getSize() { return size; }
    public double getPrice() { return price; }

    public String getDisplayName() {
        if (size == null || size.isEmpty()) return name;
        return name + " (" + size + ")";
    }
}