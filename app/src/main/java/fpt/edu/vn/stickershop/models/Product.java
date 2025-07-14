package fpt.edu.vn.stickershop.models;

public class Product {
    private int id;
    private String name;
    private double price;
    private String imageUrl;
    private String type;

    public Product(int id, String name, double price, String imageUrl, String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getType() {
        return type;
    }
}