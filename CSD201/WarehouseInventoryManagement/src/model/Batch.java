package model;

import java.time.LocalDate;

/**
 *
 * @author Group4_SE2023
 */
public class Batch {

    private String batchId;          // Mã lô hàng
    private String barcode;          // Mã vạch sản phẩm thuộc lô này
    private int quantity;            // Số lượng tồn hiện tại trong lô
    private double importPrice;      // Giá nhập của lô này
    private LocalDate manufacturingDate; // Ngày sản xuất
    private LocalDate expiryDate;        // Hạn sử dụng
    private LocalDate importDate;        // Ngày nhập kho
    
    // TODO: Constructors, Getters & Setters
    public Batch() {    
    }

    public Batch(String batchId, String barcode, int quantity, double importPrice, LocalDate manufacturingDate, LocalDate expiryDate, LocalDate importDate) {
        this.batchId = batchId;
        this.barcode = barcode;
        this.quantity = quantity;
        this.importPrice = importPrice;
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
        this.importDate = importDate;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(double importPrice) {
        this.importPrice = importPrice;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(LocalDate manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDate importDate) {
        this.importDate = importDate;
    }
    
    // Các hàm nghiệp vụ của riêng lô hàng
    public boolean isValidQuantity(int amount) {
        return amount > 0 && amount <= quantity;
    }

    public void reduceQuantity(int amount) {
        if(isValidQuantity(amount)){
            quantity = quantity - amount;
        }
    }

    public boolean isEmptyBatch() {
        return quantity == 0;
    }

    public boolean isExpired(LocalDate today) {
        return expiryDate != null && expiryDate.isBefore(today);
    }
}
