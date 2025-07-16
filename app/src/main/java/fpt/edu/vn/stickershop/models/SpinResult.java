package fpt.edu.vn.stickershop.models;

import java.util.List;

public class SpinResult {
    private List<WheelItem> wonItems;
    private double totalCost;
    private int spinCount;

    public SpinResult(List<WheelItem> wonItems, double totalCost, int spinCount) {
        this.wonItems = wonItems;
        this.totalCost = totalCost;
        this.spinCount = spinCount;
    }

    // Getters and setters
    public List<WheelItem> getWonItems() { return wonItems; }
    public void setWonItems(List<WheelItem> wonItems) { this.wonItems = wonItems; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public int getSpinCount() { return spinCount; }
    public void setSpinCount(int spinCount) { this.spinCount = spinCount; }
}