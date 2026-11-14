package view;

import constants.StoreInfo;
import datastructure.BatchNode;
import datastructure.BatchQueue;
import java.time.LocalDate;
import java.util.List;
import model.Batch;
import model.Product;
import model.StockTransaction;
import util.InputValidator;

public class ConsoleView {

    public int showMainMenuAndGetChoice() {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("   " + StoreInfo.STORE_NAME);
        System.out.println("==========================================");
        System.out.println("1. Product Management");
        System.out.println("2. Stock In / Stock Out (by batch)");
        System.out.println("3. Statistics & Reports");
        System.out.println("0. Exit");
        System.out.println("------------------------------------------");
        return InputValidator.readIntInRange("Enter your choice: ", 0, 3);
    }

    public int showWarehouseSubMenu() {
        System.out.println("\n--- STOCK IN / STOCK OUT ---");
        System.out.println("1. Receive new batch (Receive Stock)");
        System.out.println("2. Issue stock by FIFO (Issue Stock)");
        System.out.println("3. Remove expired batches");
        System.out.println("0. Back");
        return InputValidator.readIntInRange("Enter your choice: ", 0, 3);
    }

    public int showProductSubMenu() {
        System.out.println("\n--- PRODUCT MANAGEMENT ---");
        System.out.println("1. Add product");
        System.out.println("2. Update product");
        System.out.println("3. Delete product");
        System.out.println("4. Search product");
        System.out.println("5. View all products");
        System.out.println("6. Save data to TXT files");
        System.out.println("7. Load data from TXT files");
        System.out.println("0. Back");
        return InputValidator.readIntInRange("Enter your choice: ", 0, 7);
    }

    public int showStatisticsSubMenu() {
        System.out.println("\n--- STATISTICS & REPORTS ---");
        System.out.println("1. Inventory report");
        System.out.println("2. Check expired batches / low stock");
        System.out.println("3. Sort products by stock quantity");
        System.out.println("4. Transaction history");
        System.out.println("0. Back");
        return InputValidator.readIntInRange("Enter your choice: ", 0, 4);
    }

    public Product inputNewProductForm() {
        System.out.println("\n--- ADD NEW PRODUCT ---");
        String barcode = InputValidator.readNonEmptyString("Barcode: ");
        String name = InputValidator.readNonEmptyString("Product name: ");
        String category = InputValidator.readNonEmptyString("Category: ");
        String unit = InputValidator.readNonEmptyString("Unit: ");
        double price = InputValidator.readPositiveDouble("Price: ");
        return new Product(barcode, name, category, unit, price);
    }

    public Batch inputNewBatchForm(String barcode) {
        System.out.println("\n--- RECEIVE NEW BATCH for product: " + barcode + " ---");
        String batchId = InputValidator.readNonEmptyString("Batch ID: ");
        int quantity = InputValidator.readPositiveInt("Quantity: ");
        double importPrice = InputValidator.readPositiveDouble("Import price: ");
        LocalDate mfgDate = InputValidator.readValidDate("Manufacturing date: ");
        LocalDate expDate = InputValidator.readExpiryDate("Expiry date: ", mfgDate);
        LocalDate importDate = LocalDate.now();

        return new Batch(batchId, barcode, quantity, importPrice, mfgDate, expDate, importDate);
    }

    public String inputBarcodePrompt(String actionName) {
        return InputValidator.readNonEmptyString("Enter barcode to " + actionName + ": ");
    }

    public void printBatchHeader() {
        System.out.printf("%-10s | %9s | %-12s | %-12s%n", "BatchID", "Remaining", "MFG", "EXP");
        System.out.println("---------------------------------------------------");
    }

    public void printProductHeader() {
        System.out.printf("%-14s | %-20s | %-12s | %-8s | %10s | %9s%n",
                "Barcode", "Name", "Category", "Unit", "Price", "Total Qty");
        System.out.println("--------------------------------------------------------------------------------------");
    }

    public void printProductRow(Product product) {
        System.out.printf("%-14s | %-20s | %-12s | %-8s | %10.2f | %9d%n",
                product.getBarcode(),
                product.getName(),
                product.getCategory(),
                product.getUnit(),
                product.getPrice(),
                product.calculateTotalQuantity());
    }

    public void printBatchRow(Batch batch) {
        System.out.printf("%-10s | %9d | %-12s | %-12s%n",
                batch.getBatchId(),
                batch.getQuantity(),
                batch.getManufacturingDate(),
                batch.getExpiryDate());
    }

    public void printProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products.");
            return;
        }
        printProductHeader();
        for (Product product : products) {
            printProductRow(product);
        }
    }

    public void printBatches(BatchQueue queue) {
        if (queue.isEmpty()) {
            System.out.println("No batches in stock.");
            return;
        }
        printBatchHeader();
        BatchNode tmp = queue.getFront();
        while (tmp != null) {
            printBatchRow(tmp.getData());
            tmp = tmp.getNext();
        }
    }

    public void printTransactionHeader() {
        System.out.printf("%-19s | %-8s | %-14s | %-8s | %6s | %-20s%n",
                "Time", "Type", "Barcode", "Batch", "Qty", "Note");
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    public void printTransactionRow(StockTransaction tx) {
        System.out.printf("%-19s | %-8s | %-14s | %-8s | %6d | %-20s%n",
                tx.getTimestamp().toString().substring(0, 19),
                tx.getType(),
                tx.getBarcode(),
                tx.getBatchId(),
                tx.getQuantity(),
                tx.getNote());
    }

    public void printTransactions(List<StockTransaction> list) {
        if (list.isEmpty()) {
            System.out.println("No transactions.");
            return;
        }
        printTransactionHeader();
        for (StockTransaction t : list) {
            printTransactionRow(t);
        }
    }

    public void showSuccessMessage(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    public void showErrorMessage(String error) {
        System.out.println("[ERROR] " + error);
    }

}
