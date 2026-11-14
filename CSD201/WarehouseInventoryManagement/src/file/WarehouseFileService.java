package file;

import controller.ProductManager;
import datastructure.BatchNode;
import datastructure.BatchQueue;
import datastructure.TransactionLog;
import java.util.ArrayList;
import java.util.List;
import model.Batch;
import model.Product;
import model.StockTransaction;

public class WarehouseFileService {

    private final ProductFile productFile;
    private final BatchFile batchFile;
    private final TransactionFile transactionFile;
    private boolean modified = false;

    public WarehouseFileService() {
        this.productFile = new ProductFile("products.txt");
        this.batchFile = new BatchFile("batches.txt");
        this.transactionFile = new TransactionFile("transactions.txt");
    }

    public WarehouseFileService(String productFileName, String batchFileName, String transactionFileName) {
        this.productFile = new ProductFile(productFileName);
        this.batchFile = new BatchFile(batchFileName);
        this.transactionFile = new TransactionFile(transactionFileName);
    }

    public boolean isModified() {
        return modified;
    }

    public void markModified() {
        this.modified = true;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public ProductFile getProductFile() {
        return productFile;
    }

    public BatchFile getBatchFile() {
        return batchFile;
    }

    public TransactionFile getTransactionFile() {
        return transactionFile;
    }

    public boolean saveAll(ProductManager productManager, TransactionLog transactionLog) {
        if (productManager == null) {
            return false;
        }

        List<Product> products = productManager.getAllProducts();
        boolean pOk = productFile.saveToFile(products);

        List<Batch> batches = new ArrayList<>();
        for (Product p : products) {
            BatchQueue q = p.getBatchQueue();
            if (q != null && !q.isEmpty()) {
                BatchNode cur = q.getFront();
                while (cur != null) {
                    batches.add(cur.getData());
                    cur = cur.getNext();
                }
            }
        }
        boolean bOk = batchFile.saveToFile(batches);

        boolean tOk = true;
        if (transactionLog != null) {
            List<StockTransaction> txs = transactionLog.getAllTransactions();
            if (txs != null) {
                tOk = transactionFile.saveToFile(txs);
            }
        }

        boolean success = pOk && bOk && tOk;
        if (success) {
            this.modified = false;
        }
        return success;
    }

    public boolean loadAll(ProductManager productManager, TransactionLog transactionLog) {
        if (productManager == null) {
            return false;
        }

        List<Product> products = productFile.readFromFile();
        if (products == null) {
            return false;
        }

        List<Batch> batches = batchFile.readFromFile();

        List<StockTransaction> txs = (transactionLog != null) ? transactionFile.readFromFile() : null;

        productManager.clear();
        if (transactionLog != null) {
            transactionLog.clear();
        }

        int addedCount = 0;
        for (Product p : products) {
            if (productManager.addProduct(p)) {
                addedCount++;
            }
        }

        int batchCount = 0;
        if (batches != null) {
            for (Batch b : batches) {
                Product p = productManager.findProductByBarcode(b.getBarcode());
                if (p != null) {
                    p.getBatchQueue().enqueue(b);
                    batchCount++;
                }
            }
        }

        if (transactionLog != null && txs != null) {
            for (StockTransaction t : txs) {
                transactionLog.recordTransaction(t);
            }
        }

        this.modified = false;
        System.out.println("[Success] Loaded " + addedCount + " product(s) and " + batchCount + " batch(es) into warehouse.");
        return true;
    }
}
