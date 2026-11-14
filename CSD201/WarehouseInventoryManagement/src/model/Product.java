package model;

import datastructure.BatchQueue;

public class Product {

    private String barcode;
    private String name;
    private String category;
    private String unit;
    private double price;
    private BatchQueue batchQueue;

    public Product() {
        this.batchQueue = new BatchQueue();
    }

    public Product(String barcode, String name, String category, String unit, double price) {
        this.barcode = barcode;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.price = price;
        this.batchQueue = new BatchQueue();
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public BatchQueue getBatchQueue() {
        return batchQueue;
    }

    public void setBatchQueue(BatchQueue batchQueue) {
        this.batchQueue = batchQueue;
    }

    public int calculateTotalQuantity() {
        return batchQueue.getTotalQuantityInQueue();
    }

}
