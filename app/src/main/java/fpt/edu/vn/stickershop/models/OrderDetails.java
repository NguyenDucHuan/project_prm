package fpt.edu.vn.stickershop.models;

import java.util.List;

public class OrderDetails {
    private int orderId;
    private String status;
    private double total;
    private String address;
    private String timestamp;
    private int itemCount;
    private List<OrderItem> orderItems;
    private Order order; // Thêm field này

    public OrderDetails(int orderId, String status, double total, String address, String timestamp, int itemCount, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.status = status;
        this.total = total;
        this.address = address;
        this.timestamp = timestamp;
        this.itemCount = itemCount;
        this.orderItems = orderItems;
    }

    // Getters and setters
    public int getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }
    public String getAddress() { return address; }
    public String getTimestamp() { return timestamp; }
    public int getItemCount() { return itemCount; }
    public List<OrderItem> getOrderItems() { return orderItems; }

    // Thêm getter/setter cho Order object
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}