package fpt.edu.vn.stickershop.models;

public class Order {
    private int id;
    private String status;
    private double total;
    private String address;

    public Order(int id, String status, double total, String address) {
        this.id = id;
        this.status = status;
        this.total = total;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public double getTotal() {
        return total;
    }

    public String getAddress() {
        return address;
    }
}