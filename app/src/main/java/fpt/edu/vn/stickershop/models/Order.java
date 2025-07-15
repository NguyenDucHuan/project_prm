package fpt.edu.vn.stickershop.models;

public class Order {
    private int id;
    private String status;
    private double total;
    private String address;
    private String timestamp;
    private int itemCount;

    public Order(int id, String status, double total, String address) {
        this(id, status, total, address, "N/A", 0);
    }

    public Order(int id, String status, double total, String address, String timestamp, int itemCount) {
        this.id = id;
        this.status = status;
        this.total = total;
        this.address = address;
        this.timestamp = timestamp;
        this.itemCount = itemCount;
    }

    // Getters
    public int getId() { return id; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }
    public String getAddress() { return address; }
    public String getTimestamp() { return timestamp; }
    public int getItemCount() { return itemCount; }
}