package controller;

import datastructure.BatchNode;
import datastructure.BatchQueue;
import datastructure.ProductBST;
import datastructure.TransactionLog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import model.Batch;
import model.Product;
import model.StockTransaction;
import model.TransactionType;

public class WarehouseManager {

    private ProductBST productTree;
    private TransactionLog transactionLog;
    private static final AtomicLong TRANSACTION_COUNTER = new AtomicLong(1);

    public WarehouseManager() {
        this.productTree = new ProductBST();
        this.transactionLog = new TransactionLog();
    }

    public WarehouseManager(ProductBST productTree, TransactionLog transactionLog) {
        this.productTree = (productTree != null) ? productTree : new ProductBST();
        this.transactionLog = (transactionLog != null) ? transactionLog : new TransactionLog();
    }

    public ProductBST getProductTree() {
        return productTree;
    }

    public void setProductTree(ProductBST productTree) {
        this.productTree = (productTree != null) ? productTree : new ProductBST();
    }

    public TransactionLog getTransactionLog() {
        return transactionLog;
    }

    public void setTransactionLog(TransactionLog transactionLog) {
        this.transactionLog = (transactionLog != null) ? transactionLog : new TransactionLog();
    }

    private String generateTransactionId(String prefix) {
        return String.format("TX-%s-%d-%04d", prefix, System.currentTimeMillis() % 1000000,
                TRANSACTION_COUNTER.getAndIncrement() % 10000);
    }

    public boolean receiveStock(String barcode, Batch newBatch, String note) {
        if (barcode == null || barcode.trim().isEmpty() || newBatch == null) {
            return false;
        }

        if (newBatch.getQuantity() <= 0) {
            return false;
        }

        String normalizedBarcode = barcode.trim();
        Product product = productTree.search(normalizedBarcode);
        if (product == null) {
            return false;
        }

        newBatch.setBarcode(normalizedBarcode);
        if (newBatch.getImportDate() == null) {
            newBatch.setImportDate(LocalDate.now());
        }
        if (newBatch.getBatchId() == null || newBatch.getBatchId().trim().isEmpty()) {
            newBatch.setBatchId("BATCH-" + System.currentTimeMillis() % 1000000);
        }
        newBatch.setBatchId(newBatch.getBatchId().trim());
        if (checkDuplicateBatchId(product, newBatch.getBatchId())) {
            return false;
        }
        if (newBatch.getManufacturingDate() != null && newBatch.getExpiryDate() != null
                && newBatch.getExpiryDate().isBefore(newBatch.getManufacturingDate())) {
            return false;
        }

        product.getBatchQueue().enqueue(newBatch);

        String txNote = (note != null && !note.trim().isEmpty()) ? note.trim() : "Inbound new batch";
        StockTransaction transaction = new StockTransaction(
                generateTransactionId("IN"),
                TransactionType.INBOUND,
                normalizedBarcode,
                newBatch.getBatchId(),
                newBatch.getQuantity(),
                LocalDateTime.now(),
                txNote
        );
        transactionLog.recordTransaction(transaction);

        return true;
    }

    public boolean issueStock(String barcode, int requestedQuantity, String note) {
        if (barcode == null || barcode.trim().isEmpty() || requestedQuantity <= 0) {
            return false;
        }
        String normalizedBarcode = barcode.trim();
        Product product = productTree.search(normalizedBarcode);
        if (product == null) {return false;}
        processExpiredBatches(normalizedBarcode, LocalDate.now());
        int totalAvailable = product.calculateTotalQuantity();
        if (totalAvailable < requestedQuantity) {
            return false;
        }
        BatchQueue queue = product.getBatchQueue();
        int remainingToIssue = requestedQuantity;
        String txNote = (note != null && !note.trim().isEmpty()) ? note.trim() : "Outbound stock via FIFO";
        while (remainingToIssue > 0 && !queue.isEmpty()) {
            Batch currentBatch = queue.peek();
            if (currentBatch == null) {
                break;
            }
            int availableInBatch = currentBatch.getQuantity();
            if (availableInBatch <= 0) {
                queue.dequeue();
                continue;
            }
            int deductAmount = Math.min(availableInBatch, remainingToIssue);
            currentBatch.reduceQuantity(deductAmount);
            remainingToIssue -= deductAmount;
            StockTransaction transaction = new StockTransaction(
                    generateTransactionId("OUT"),
                    TransactionType.OUTBOUND,
                    normalizedBarcode,
                    currentBatch.getBatchId(),
                    deductAmount,
                    LocalDateTime.now(),
                    txNote
            );
            transactionLog.recordTransaction(transaction);
            if (currentBatch.isEmptyBatch()) {
                queue.dequeue();
            }
        }
        return true;
    }

    private boolean checkDuplicateBatchId(Product product, String batchId) {
        BatchNode current = product.getBatchQueue().getFront();
        while (current != null) {
            if (current.getData().getBatchId().equalsIgnoreCase(batchId)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    public List<Batch> processExpiredBatches(String barcode, LocalDate checkDate) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Product product = productTree.search(barcode.trim());
        if (product == null) {
            return new ArrayList<>();
        }

        LocalDate referenceDate = (checkDate != null) ? checkDate : LocalDate.now();
        List<Batch> expiredBatches = product.getBatchQueue().removeExpired(referenceDate);

        if (expiredBatches != null && !expiredBatches.isEmpty()) {
            for (Batch batch : expiredBatches) {
                StockTransaction transaction = new StockTransaction(
                        generateTransactionId("EXP"),
                        TransactionType.EXPIRED,
                        product.getBarcode(),
                        batch.getBatchId(),
                        batch.getQuantity(),
                        LocalDateTime.now(),
                        "Disposal of expired batch"
                );
                transactionLog.recordTransaction(transaction);
            }
            return expiredBatches;
        }

        return new ArrayList<>();
    }

    public BatchQueue getProductBatches(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }
        Product product = productTree.search(barcode.trim());
        return (product != null) ? product.getBatchQueue() : null;
    }
}
