
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class WarehouseSystem {

    private ProductBST productCatalog;
    private Map<String, FIFOInventory> fifoInventories;
    private Map<String, LIFOInventory> lifoInventories;
    private Map<String, String> strategyMap;
    private int batchCounter = 1;

    public WarehouseSystem() {
        productCatalog  = new ProductBST();
        fifoInventories = new HashMap<>();
        lifoInventories = new HashMap<>();
        strategyMap     = new HashMap<>();
    }

    public boolean registerProduct(Product product, String strategy) {
        if (productCatalog.insert(product)) {
            String s = strategy.toUpperCase();
            strategyMap.put(product.getBarcode(), s);
            if ("FIFO".equals(s)) {
                fifoInventories.put(product.getBarcode(), new FIFOInventory(product.getBarcode()));
            } else {
                lifoInventories.put(product.getBarcode(), new LIFOInventory(product.getBarcode()));
            }
            System.out.printf("  [OK] Dang ky: %-22s [Chien luoc: %s]\n",
                    product.getName(), s);
            return true;
        }
        return false;
    }

    public void importGoods(String barcode, int quantity, LocalDate expiryDate) {
        Product p = productCatalog.search(barcode);
        if (p == null) {
            System.out.println("  [ERR] San pham '" + barcode + "' khong ton tai trong danh muc!");
            return;
        }

        String batchId = "B" + String.format("%03d", batchCounter++);
        ProductBatch batch = new ProductBatch(batchId, quantity, LocalDate.now(), expiryDate);

        System.out.printf("\n  >> Nhap kho | SP: %-22s | Lo: %s\n", p.getName(), batchId);
        if ("FIFO".equals(strategyMap.get(barcode))) {
            fifoInventories.get(barcode).importBatch(batch);
        } else {
            lifoInventories.get(barcode).importBatch(batch);
        }
    }

    public int exportGoods(String barcode, int quantity) {
        Product p = productCatalog.search(barcode);
        if (p == null) {
            System.out.println("  [ERR] San pham '" + barcode + "' khong ton tai!");
            return 0;
        }

        String strategy = strategyMap.get(barcode);
        System.out.printf("\n  >> Xuat kho | SP: %-22s | Yeu cau: %d sp [%s]\n",
                p.getName(), quantity, strategy);

        if ("FIFO".equals(strategy)) {
            return fifoInventories.get(barcode).exportGoods(quantity);
        } else {
            return lifoInventories.get(barcode).exportGoods(quantity);
        }
    }

    public void searchProduct(String barcode) {
        System.out.println("\n  >> Tim kiem barcode: " + barcode);
        Product p = productCatalog.search(barcode);
        if (p == null) {
            System.out.println("  [ERR] Khong tim thay san pham!");
            return;
        }
        System.out.println("  [OK] Tim thay: " + p);

        String strategy = strategyMap.get(barcode);
        int qty = "FIFO".equals(strategy)
                ? fifoInventories.get(barcode).getTotalQuantity()
                : lifoInventories.get(barcode).getTotalQuantity();
        System.out.printf("  Chien luoc: %s | Ton kho: %d sp\n", strategy, qty);
    }

    public void displayCatalog() {
        System.out.println("\n==================================================================");
        productCatalog.inOrderDisplay();
    }

    public void displayBSTStructure() {
        System.out.println("\n==================================================================");
        System.out.println("  Cau truc cay nhi phan tim kiem (BST):");
        productCatalog.displayTree();
    }

    public void displayInventory(String barcode) {
        Product p = productCatalog.search(barcode);
        if (p == null) {
            System.out.println("  [ERR] San pham khong ton tai!");
            return;
        }
        System.out.printf("\n  >> Ton kho chi tiet: %s (%s)\n", p.getName(), barcode);
        String strategy = strategyMap.get(barcode);
        if ("FIFO".equals(strategy)) {
            fifoInventories.get(barcode).display();
        } else {
            lifoInventories.get(barcode).display();
        }
    }

    public void displayAllInventory() {
        System.out.println("\n==================================================================");
        System.out.println("  TONG HOP TON KHO TOAN KHO:");
        System.out.println("  +-----------+------------------------+------+---------------+");
        System.out.printf("  | %-9s | %-22s | %-4s | %-13s |\n", "MA VACH", "TEN SP", "CS", "TON KHO");
        System.out.println("  +-----------+------------------------+------+---------------+");
        for (Map.Entry<String, String> entry : strategyMap.entrySet()) {
            String bc  = entry.getKey();
            String st  = entry.getValue();
            Product p  = productCatalog.search(bc);
            int qty    = "FIFO".equals(st)
                    ? fifoInventories.get(bc).getTotalQuantity()
                    : lifoInventories.get(bc).getTotalQuantity();
            System.out.printf("  | %-9s | %-22s | %-4s | %6d sp      |\n",
                    bc, p.getName(), st, qty);
        }
        System.out.println("  +-----------+------------------------+------+---------------+");
    }

    public void removeProduct(String barcode) {
        System.out.printf("\n  >> Xoa san pham barcode: %s\n", barcode);
        if (productCatalog.delete(barcode)) {
            fifoInventories.remove(barcode);
            lifoInventories.remove(barcode);
            strategyMap.remove(barcode);
            System.out.println("  [OK] Da xoa san pham khoi he thong.");
        }
    }
}
