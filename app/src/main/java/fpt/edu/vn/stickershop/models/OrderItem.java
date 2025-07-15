package fpt.edu.vn.stickershop.models;

public class OrderItem {
    private int productId;
    private String productName;
    private String productImage;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public OrderItem(int productId, String productName, String productImage,
                     int quantity, double unitPrice, double totalPrice) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductImage() { return productImage; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return totalPrice; }
}