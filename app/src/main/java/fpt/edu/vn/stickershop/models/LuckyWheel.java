package fpt.edu.vn.stickershop.models;

import java.util.List;

public class LuckyWheel {
    private int id;
    private String name;
    private double cost;
    private String description;
    private List<WheelItem> items;

    public LuckyWheel(int id, String name, double cost, String description, List<WheelItem> items) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.items = items;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<WheelItem> getItems() { return items; }
    public void setItems(List<WheelItem> items) { this.items = items; }
}