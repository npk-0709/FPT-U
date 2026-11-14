package controller;

import constants.StoreInfo;
import datastructure.BatchNode;
import datastructure.ProductBST;
import datastructure.TransactionLog;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import model.Batch;
import model.Product;
import model.StockTransaction;
import model.TransactionType;
import view.ConsoleView;

public class StatisticsManager {

    private ProductBST productTree;
    private TransactionLog transactionLog;

    public StatisticsManager() {
        this.productTree = new ProductBST();
        this.transactionLog = new TransactionLog();
    }

    public StatisticsManager(ProductBST productTree, TransactionLog transactionLog) {
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

    public void generateInventorySummary() {
        List<Product> products = productTree.inOrderList();

        System.out.println("\n=========================================================================================================");
        System.out.println("                                      " + StoreInfo.STORE_NAME);
        System.out.println("                                      Address: " + StoreInfo.STORE_ADDRESS);
        System.out.println("                                      INVENTORY SUMMARY REPORT");
        System.out.println("                                      Created Date: " + LocalDate.now());
        System.out.println("=========================================================================================================");

        if (products == null || products.isEmpty()) {
            System.out.println(">> Notice: Currently there are no products in the warehouse.");
            System.out.println("=========================================================================================================\n");
            return;
        }

        System.out.printf("%-14s | %-22s | %-12s | %-8s | %12s | %10s | %15s%n",
                "Barcode", "Product Name", "Category", "Unit", "Unit Price", "In Stock", "Total Value");
        System.out.println("---------------------------------------------------------------------------------------------------------");

        int totalSku = 0;
        long totalStockQuantity = 0;
        double totalInventoryValue = 0;
        int totalBatches = 0;

        for (Product product : products) {
            int qty = product.calculateTotalQuantity();
            double val = qty * product.getPrice();

            System.out.printf("%-14s | %-22s | %-12s | %-8s | %12.2f | %10d | %15.2f%n",
                    product.getBarcode(),
                    product.getName(),
                    product.getCategory(),
                    product.getUnit(),
                    product.getPrice(),
                    qty,
                    val);

            totalSku++;
            totalStockQuantity += qty;
            totalInventoryValue += val;

            if (product.getBatchQueue() != null) {
                totalBatches += product.getBatchQueue().getSize();
            }
        }

        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.println("SUMMARY STATISTICS:");
        System.out.printf(" - Total Product SKUs      : %,d%n", totalSku);
        System.out.printf(" - Total Stock Quantity    : %,d%n", totalStockQuantity);
        System.out.printf(" - Total Inventory Value   : %,.2f%n", totalInventoryValue);
        System.out.printf(" - Total Batches in Stock  : %,d%n", totalBatches);
        System.out.println("=========================================================================================================\n");
    }

    public void checkExpiredOrLowStockBatches() {
        LocalDate today = LocalDate.now();
        List<Product> products = productTree.inOrderList();

        System.out.println("\n=========================================================================================================");
        System.out.println("                         EXPIRED & LOW STOCK BATCHES ALERT");
        System.out.println("=========================================================================================================");

        if (products == null || products.isEmpty()) {
            System.out.println(">> Notice: No products in warehouse to check.");
            System.out.println("=========================================================================================================\n");
            return;
        }

        System.out.println("[1] LOW STOCK PRODUCTS LIST (Warning Threshold: <= " + StoreInfo.LOW_STOCK_THRESHOLD + "):");
        List<Product> lowStockProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.calculateTotalQuantity() <= StoreInfo.LOW_STOCK_THRESHOLD) {
                lowStockProducts.add(product);
            }
        }

        if (lowStockProducts.isEmpty()) {
            System.out.println("    -> All products maintain safe inventory levels (> " + StoreInfo.LOW_STOCK_THRESHOLD + ").");
        } else {
            System.out.printf("    %-14s | %-22s | %-10s | %-18s%n", "Barcode", "Product Name", "In Stock", "Alert Level");
            System.out.println("    --------------------------------------------------------------------");
            for (Product p : lowStockProducts) {
                int qty = p.calculateTotalQuantity();
                String alert = (qty == 0) ? "[OUT OF STOCK]" : "[LOW STOCK WARNING]";
                System.out.printf("    %-14s | %-22s | %10d | %-18s%n",
                        p.getBarcode(), p.getName(), qty, alert);
            }
        }

        System.out.println();

        System.out.println("[2] EXPIRED OR EXPIRING SOON BATCHES (<= 30 days):");
        boolean foundBatchWarning = false;

        System.out.printf("    %-14s | %-18s | %-10s | %6s | %-10s | %-10s | %-32s%n",
                "Barcode", "Product Name", "Batch ID", "Qty", "MFG Date", "EXP Date", "Status");
        System.out.println("    ------------------------------------------------------------------------------------------------");

        for (Product product : products) {
            if (product.getBatchQueue() == null || product.getBatchQueue().isEmpty()) {
                continue;
            }

            BatchNode current = product.getBatchQueue().getFront();
            while (current != null) {
                Batch batch = current.getData();
                if (batch != null && batch.getExpiryDate() != null) {
                    if (batch.isExpired(today)) {
                        foundBatchWarning = true;
                        long daysOverdue = ChronoUnit.DAYS.between(batch.getExpiryDate(), today);
                        System.out.printf("    %-14s | %-18s | %-10s | %6d | %-10s | %-10s | %-32s%n",
                                product.getBarcode(),
                                truncate(product.getName(), 18),
                                batch.getBatchId(),
                                batch.getQuantity(),
                                batch.getManufacturingDate(),
                                batch.getExpiryDate(),
                                "[EXPIRED - " + daysOverdue + " day(s) overdue]");
                    } else if (!batch.getExpiryDate().isAfter(today.plusDays(30))) {
                        foundBatchWarning = true;
                        long daysRemaining = ChronoUnit.DAYS.between(today, batch.getExpiryDate());
                        System.out.printf("    %-14s | %-18s | %-10s | %6d | %-10s | %-10s | %-32s%n",
                                product.getBarcode(),
                                truncate(product.getName(), 18),
                                batch.getBatchId(),
                                batch.getQuantity(),
                                batch.getManufacturingDate(),
                                batch.getExpiryDate(),
                                "[EXPIRING SOON - " + daysRemaining + " day(s) left]");
                    }
                }
                current = current.getNext();
            }
        }

        if (!foundBatchWarning) {
            System.out.println("    -> Warehouse safe! No expired or expiring batches in the next 30 days.");
        }

        System.out.println("=========================================================================================================\n");
    }

    public void displaySortedProductsByQuantity() {
        List<Product> products = getSortedProductsByQuantity(true);

        if (products.isEmpty()) {
            System.out.println(">> Notice: No products to sort.");
            return;
        }

        System.out.println("\n======================================================================================");
        System.out.println("                 PRODUCTS SORTED BY QUANTITY DESCENDING (BUBBLE SORT)");
        System.out.println("======================================================================================");
        System.out.printf("%-5s | %-14s | %-20s | %-12s | %-8s | %10s | %9s%n",
                "Rank", "Barcode", "Product Name", "Category", "Unit", "Price", "Total Qty");
        System.out.println("--------------------------------------------------------------------------------------");

        int rank = 1;
        for (Product product : products) {
            System.out.printf("#%-4d | %-14s | %-20s | %-12s | %-8s | %10.2f | %9d%n",
                    rank++,
                    product.getBarcode(),
                    truncate(product.getName(), 20),
                    product.getCategory(),
                    product.getUnit(),
                    product.getPrice(),
                    product.calculateTotalQuantity());
        }
        System.out.println("======================================================================================\n");
    }

    public List<Product> getSortedProductsByQuantity(boolean descending) {
        List<Product> list = new ArrayList<>(productTree.inOrderList());
        bubbleSort(list, descending);
        return list;
    }

    private void bubbleSort(List<Product> list, boolean descending){
        int n = list.size();
        for (int i = 0; i < n -1; i++){
            for (int j = 0; j < n -1 - i ; j++){
                if(descending){
                    if(list.get(j).calculateTotalQuantity() < list.get(j+1).calculateTotalQuantity()){
                        Product tmp = list.get(j);
                        list.set(j, list.get(j+1));
                        list.set(j+1, tmp);
                    }
                }else{
                    if(list.get(j).calculateTotalQuantity() > list.get(j+1).calculateTotalQuantity()){
                        Product tmp = list.get(j);
                        list.set(j, list.get(j+1));
                        list.set(j+1, tmp);
                    }
                }
            }
        }
    }

    public void generateTransactionReport(String barcode) {
        ConsoleView view = new ConsoleView();

        if (barcode == null || barcode.trim().isEmpty() || barcode.trim().equalsIgnoreCase("ALL")) {

            System.out.println("\n=========================================================================================");
            System.out.println("                        COMPLETE WAREHOUSE TRANSACTION REPORT");
            System.out.println("=========================================================================================");

            List<StockTransaction> allTx = transactionLog.getAllTransactions();
            if (allTx == null || allTx.isEmpty()) {
                System.out.println(">> Notice: No transactions have been recorded in the system yet.");
                System.out.println("=========================================================================================\n");
                return;
            }

            view.printTransactions(allTx);
            System.out.println(">> Total recorded transactions: " + allTx.size());
            System.out.println("=========================================================================================\n");
            return;
        }

        String normalizedBarcode = barcode.trim();
        Product product = productTree.search(normalizedBarcode);

        int totalInbound = transactionLog.calculateTotalByType(normalizedBarcode, TransactionType.INBOUND);
        int totalOutbound = transactionLog.calculateTotalByType(normalizedBarcode, TransactionType.OUTBOUND);
        int totalExpired = transactionLog.calculateTotalByType(normalizedBarcode, TransactionType.EXPIRED);

        System.out.println("\n=========================================================================================");
        System.out.println("                       TRANSACTION REPORT FOR PRODUCT: " + normalizedBarcode);
        if (product != null) {
            System.out.println("Product Name : " + product.getName() + " | Category: " + product.getCategory());
            System.out.println("Unit         : " + product.getUnit() + " | Price: " + product.getPrice());
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf(" - Total INBOUND Quantity   : %,d %s%n", totalInbound, (product != null ? product.getUnit() : ""));
        System.out.printf(" - Total OUTBOUND Quantity  : %,d %s%n", totalOutbound, (product != null ? product.getUnit() : ""));
        System.out.printf(" - Total EXPIRED Quantity   : %,d %s%n", totalExpired, (product != null ? product.getUnit() : ""));
        System.out.printf(" - Current Actual Stock     : %,d %s%n",
                (product != null ? product.calculateTotalQuantity() : (totalInbound - totalOutbound - totalExpired)),
                (product != null ? product.getUnit() : ""));
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("TRANSACTION HISTORY DETAILS:");

        List<StockTransaction> txList = transactionLog.getTransactionsByProduct(normalizedBarcode);
        if (txList == null || txList.isEmpty()) {
            System.out.println(">> No transaction history recorded for this product.");
        } else {
            view.printTransactions(txList);
        }

        System.out.println("=========================================================================================\n");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if(text.length() <= maxLength){
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
