package com.example.theroasteryhouse.models;
public class MenuItem {
    private int id;
    private String name;
    private String type;
    private double priceS;
    private double priceM;
    private double priceL;

    private double priceSingle;

    public MenuItem(int id, String name, String type, double priceS, double priceM, double priceL) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.priceS = priceS;
        this.priceM = priceM;
        this.priceL = priceL;
    }

    public MenuItem(int id, String name, String type, double priceSingle) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.priceSingle = priceSingle;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }

    public double getPriceS() { return priceS; }
    public double getPriceM() { return priceM; }
    public double getPriceL() { return priceL; }
    public double getPriceSingle() { return priceSingle; }
}