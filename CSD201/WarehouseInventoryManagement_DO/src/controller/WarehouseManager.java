package controller;

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

/**
 * Controller managing warehouse operations: receiving stock and FIFO stock issue.
 * 
 * @author Group4_SE2023
 */
public class WarehouseManager {

    private ProductBST productTree;
    private TransactionLog transactionLog;
    private static final AtomicLong TRANSACTION_COUNTER = new AtomicLong(1);

    /**
     * Default constructor.
     */
    public WarehouseManager() {
        this.productTree = new ProductBST();
        this.transactionLog = new TransactionLog();
    }

    /**
     * Constructor injecting shared ProductBST and TransactionLog instances.
     *
     * @param productTree    The shared product tree
     * @param transactionLog The shared transaction log
     */
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

    /**
     * Generates a unique transaction identifier.
     *
     * @param prefix Transaction type prefix (e.g., "IN", "OUT", "EXP")
     * @return Unique transaction ID
     */
    private String generateTransactionId(String prefix) {
        return String.format("TX-%s-%d-%04d", prefix, System.currentTimeMillis() % 1000000,
                TRANSACTION_COUNTER.getAndIncrement() % 10000);
    }

    /**
     * Receives a new stock batch for a product and logs an INBOUND transaction.
     *
     * @param barcode  The barcode of the product receiving stock
     * @param newBatch The new batch object containing batch details
     * @param note     Optional note/comment for the transaction
     * @return true if received successfully, false if product not found or batch invalid
     */
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

        // Ensure batch attributes are properly configured
        newBatch.setBarcode(normalizedBarcode);
        if (newBatch.getImportDate() == null) {
            newBatch.setImportDate(LocalDate.now());
        }
        if (newBatch.getBatchId() == null || newBatch.getBatchId().trim().isEmpty()) {
            newBatch.setBatchId("BATCH-" + System.currentTimeMillis() % 1000000);
        }

        // 1. Enqueue batch to product's BatchQueue
        product.getBatchQueue().enqueue(newBatch);

        // 2. Create and record INBOUND transaction
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

    /**
     * Issues stock using FIFO (First-In, First-Out) strategy across product batches.
     * Depletes older batches first, only dequeuing a batch when its quantity reaches 0.
     * Records OUTBOUND transactions for each impacted batch.
     *
     * @param barcode           The barcode of the product to issue
     * @param requestedQuantity The total quantity requested to issue
     * @param note              Optional note/comment for the transaction
     * @return true if stock was issued successfully, false if not found or insufficient stock
     */
    public boolean issueStock(String barcode, int requestedQuantity, String note) {
        if (barcode == null || barcode.trim().isEmpty() || requestedQuantity <= 0) {
            return false;
        }

        String normalizedBarcode = barcode.trim();
        Product product = productTree.search(normalizedBarcode);
        if (product == null) {
            return false;
        }

        // 1. Check total inventory availability
        int totalAvailable = product.calculateTotalQuantity();
        if (totalAvailable < requestedQuantity) {
            return false;
        }

        // 2. FIFO loop over batchQueue
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
                // Empty batch, remove and proceed
                queue.dequeue();
                continue;
            }

            int deductAmount = Math.min(availableInBatch, remainingToIssue);

            // Deduct quantity from this batch
            currentBatch.reduceQuantity(deductAmount);
            remainingToIssue -= deductAmount;

            // Record transaction for this batch
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

            // Dequeue only if batch is completely emptied
            if (currentBatch.isEmptyBatch()) {
                queue.dequeue();
            }
        }

        return true;
    }

    /**
     * Removes expired batches for a product and logs EXPIRED transactions.
     *
     * @param barcode   The product barcode
     * @param checkDate The reference date (usually LocalDate.now())
     * @return List of batches that were removed due to expiration
     */
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

    /**
     * Gets the batch queue of a product.
     *
     * @param barcode The product barcode
     * @return The product's BatchQueue, or null if product not found
     */
    public BatchQueue getProductBatches(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }
        Product product = productTree.search(barcode.trim());
        return (product != null) ? product.getBatchQueue() : null;
    }
}
