package model;

import java.time.LocalDateTime;

public class StockTransaction {

    private String transactionId;
    private TransactionType type;
    private String barcode;
    private String batchId;
    private int quantity;
    private LocalDateTime timestamp;
    private String note;

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
