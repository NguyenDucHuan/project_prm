package fpt.edu.vn.stickershop.models;

public class CartItem {
    private int id;
    private int productId;
    private String productName;
    private double productPrice;
    private String productImageUrl;
    private String productType;
    private int quantity;

    public CartItem(int id, int productId, String productName, double productPrice,
                    String productImageUrl, String productType, int quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.productType = productType;
        this.quantity = quantity;
    }

    // Getters
    public int getId() { return id; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getProductPrice() { return productPrice; }
    public String getProductImageUrl() { return productImageUrl; }
    public String getProductType() { return productType; }
    public int getQuantity() { return quantity; }

    // Setters
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() {
        return productPrice * quantity;
    }
}