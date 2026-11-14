import file.BatchFile;
import file.ProductFile;
import file.TransactionFile;
import file.WarehouseFileService;
import controller.ProductManager;
import datastructure.ProductBST;
import datastructure.TransactionLog;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Batch;
import model.Product;
import model.StockTransaction;
import model.TransactionType;

public class TestFileIO {

    public static void main(String[] args) {
        System.out.println("=== TESTING FILE I/O TXT (LAB211 PATTERN) ===");

        // Test ProductFile
        ProductFile pf = new ProductFile("test_products.txt");
        List<Product> pList = new ArrayList<>();
        pList.add(new Product("P01", "Sua tuoi Vinamilk", "Dairy", "hop", 12000));
        pList.add(new Product("P02", "Gao ST25", "Food", "kg", 35000));
        boolean pSaved = pf.saveToFile(pList);
        System.out.println("ProductFile save: " + pSaved);
        List<Product> pRead = pf.readFromFile();
        System.out.println("ProductFile read count: " + (pRead != null ? pRead.size() : "null"));

        // Test BatchFile
        BatchFile bf = new BatchFile("test_batches.txt");
        List<Batch> bList = new ArrayList<>();
        bList.add(new Batch("B01", "P01", 50, 10000, LocalDate.now().minusDays(10), LocalDate.now().plusDays(100), LocalDate.now()));
        bList.add(new Batch("B02", "P02", 30, 30000, LocalDate.now().minusDays(5), LocalDate.now().plusDays(200), LocalDate.now()));
        boolean bSaved = bf.saveToFile(bList);
        System.out.println("BatchFile save: " + bSaved);
        List<Batch> bRead = bf.readFromFile();
        System.out.println("BatchFile read count: " + (bRead != null ? bRead.size() : "null"));

        // Test TransactionFile
        TransactionFile tf = new TransactionFile("test_transactions.txt");
        List<StockTransaction> tList = new ArrayList<>();
        tList.add(new StockTransaction("TX01", TransactionType.INBOUND, "P01", "B01", 50, LocalDateTime.now(), "Nhap kho ban dau"));
        boolean tSaved = tf.saveToFile(tList);
        System.out.println("TransactionFile save: " + tSaved);
        List<StockTransaction> tRead = tf.readFromFile();
        System.out.println("TransactionFile read count: " + (tRead != null ? tRead.size() : "null"));

        // Test WarehouseFileService
        ProductBST bst = new ProductBST();
        TransactionLog log = new TransactionLog();
        ProductManager pm = new ProductManager(bst);
        pm.addProduct(pList.get(0));
        pList.get(0).getBatchQueue().enqueue(bList.get(0));
        log.recordTransaction(tList.get(0));

        WarehouseFileService service = new WarehouseFileService("test_svc_p.txt", "test_svc_b.txt", "test_svc_t.txt");
        boolean svcSvd = service.saveAll(pm, log);
        System.out.println("WarehouseFileService saveAll: " + svcSvd);

        ProductBST bst2 = new ProductBST();
        TransactionLog log2 = new TransactionLog();
        ProductManager pm2 = new ProductManager(bst2);
        boolean svcLdd = service.loadAll(pm2, log2);
        System.out.println("WarehouseFileService loadAll: " + svcLdd);
        System.out.println("Loaded product in bst2: " + (pm2.findProductByBarcode("P01") != null ? pm2.findProductByBarcode("P01").getName() : "not found"));
        System.out.println("Total quantity in P01: " + pm2.findProductByBarcode("P01").calculateTotalQuantity());
        System.out.println("Transactions in log2: " + (log2.getAllTransactions() != null ? log2.getAllTransactions().size() : 0));

        // Clean up test files
        new File("test_products.txt").delete();
        new File("test_batches.txt").delete();
        new File("test_transactions.txt").delete();
        new File("test_svc_p.txt").delete();
        new File("test_svc_b.txt").delete();
        new File("test_svc_t.txt").delete();

        System.out.println("=== ALL TESTS FINISHED SUCCESSFULLY! ===");
    }
}
