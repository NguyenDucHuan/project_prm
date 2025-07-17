package fpt.edu.vn.stickershop.models;

public class InventoryItem {
    private int id;
    private int userId;
    private int productId;
    private String productName;
    private String productImage;
    private int quantity;
    private String dateObtained;
    private int withdrawQuantity = 0; // Thêm field này

    public InventoryItem(int id, int userId, int productId, String productName, String productImage, int quantity, String dateObtained) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.quantity = quantity;
        this.dateObtained = dateObtained;
    }

    // Existing getters and setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDateObtained() { return dateObtained; }
    public void setDateObtained(String dateObtained) { this.dateObtained = dateObtained; }

    // New getter and setter for withdraw quantity
    public int getWithdrawQuantity() { return withdrawQuantity; }
    public void setWithdrawQuantity(int withdrawQuantity) {
        this.withdrawQuantity = Math.max(0, Math.min(withdrawQuantity, quantity));
    }
}