package model;

import java.time.LocalDate;

public class Batch {

    private String batchId;
    private String barcode;
    private int quantity;
    private double importPrice;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private LocalDate importDate;

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
