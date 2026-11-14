package pe.model;

import java.io.Serializable;
import java.sql.Date;

public class OrderDTO implements Serializable {
    private String orderID;
    private Date orderDate;
    private String customer;
    private String address;
    private double totalAmount;
    private String status;

    public OrderDTO() {
    }

    public OrderDTO(String orderID, Date orderDate, String customer, String address, double totalAmount, String status) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.customer = customer;
        this.address = address;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
