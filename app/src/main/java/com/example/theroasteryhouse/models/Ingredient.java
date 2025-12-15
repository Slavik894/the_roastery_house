package com.example.theroasteryhouse.models;

public class Ingredient {
    private int id;
    private String name;
    private String info;
    private String imageUri;
    private String type;
    private double price;


    public Ingredient(int id, String name, String info, String imageUri) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.imageUri = imageUri;
        this.type = type;
        this.price = price;

    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getInfo() { return info; }
    public String getImageUri() { return imageUri; }
    public String getType() { return type; }
    public double getPrice() { return price; }

}