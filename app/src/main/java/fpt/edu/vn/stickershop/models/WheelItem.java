package fpt.edu.vn.stickershop.models;

public class WheelItem {
    private int id;
    private int wheelId;
    private int productId;
    private String productName;
    private String productImage;
    private double probability;
    private int quantity;

    public WheelItem(int id, int wheelId, int productId, String productName, String productImage, double probability, int quantity) {
        this.id = id;
        this.wheelId = wheelId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.probability = probability;
        this.quantity = quantity;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getWheelId() { return wheelId; }
    public void setWheelId(int wheelId) { this.wheelId = wheelId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public double getProbability() { return probability; }
    public void setProbability(double probability) { this.probability = probability; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
