package model;

import java.time.LocalDateTime;

/**
 *
 * @author Group4_SE2023
 */
public class StockTransaction {

    private String transactionId;    // Mã giao dịch
    private TransactionType type;    
    private String barcode;          // Mã sản phẩm
    private String batchId;          // Mã lô hàng bị tác động
    private int quantity;            // Số lượng thay đổi
    private LocalDateTime timestamp; // Thời gian giao dịch
    private String note;             // Ghi chú thêm

    public StockTransaction() {
    }

    public StockTransaction(String transactionId, TransactionType type, String barcode, String batchId, int quantity, LocalDateTime timestamp, String note) {
        this.transactionId = transactionId;
        this.type = type;
        this.barcode = barcode;
        this.batchId = batchId;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.note = note;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
