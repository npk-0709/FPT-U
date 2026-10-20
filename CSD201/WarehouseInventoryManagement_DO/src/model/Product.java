package model;

import datastructure.BatchQueue;

/**
 *
 * @author Group4_SE2023
 */
public class Product {

    private String barcode;          // Mã định danh (Khóa chính để so sánh trong BST)
    private String name;             // Tên sản phẩm
    private String category;         // Danh mục
    private String unit;             // Đơn vị tính (Thùng, Hộp, Cái...)
    private double price;            // Giá bán niêm yết
    private BatchQueue batchQueue;   // Hàng đợi chứa các lô hàng của sản phẩm này

    // TODO: Constructors (nhớ khởi tạo this.batchQueue = new BatchQueue()), Getters & Setters
    
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

    // Hàm tính tổng tồn kho từ các lô hàng
    public int calculateTotalQuantity() {
        return batchQueue.getTotalQuantityInQueue();
    }

}
