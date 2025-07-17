package fpt.edu.vn.stickershop.models;

public class Order {
    private int id;
    private String status;
    private double total;
    private String address;
    private String timestamp;
    private int itemCount;
    private String orderType; // Thêm field này

    public Order(int id, String status, double total, String address) {
        this(id, status, total, address, "N/A", 0, "PURCHASE");
    }

    public Order(int id, String status, double total, String address, String timestamp, int itemCount) {
        this(id, status, total, address, timestamp, itemCount, "PURCHASE");
    }

    public Order(int id, String status, double total, String address, String timestamp, int itemCount, String orderType) {
        this.id = id;
        this.status = status;
        this.total = total;
        this.address = address;
        this.timestamp = timestamp;
        this.itemCount = itemCount;
        this.orderType = orderType != null ? orderType : "PURCHASE";
    }

    // Getters
    public int getId() { return id; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }
    public String getAddress() { return address; }
    public String getTimestamp() { return timestamp; }
    public int getItemCount() { return itemCount; }
    public String getOrderType() { return orderType; }

    // Methods to check order type
    public boolean isPurchaseOrder() {
        return "PURCHASE".equals(orderType);
    }

    public boolean isWithdrawalOrder() {
        return "WITHDRAWAL".equals(orderType);
    }

    // Setters if needed
    public void setOrderType(String orderType) {
        this.orderType = orderType != null ? orderType : "PURCHASE";
    }
}