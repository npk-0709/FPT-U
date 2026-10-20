package view;

import datastructure.BatchNode;
import datastructure.BatchQueue;
import java.util.List;
import model.Batch;
import model.Product;
import model.StockTransaction;

/**
 *
 * @author Group4_SE2023
 */
public class ConsoleView {

    // ==========================================
    // 1. NHÓM HÀM HIỂN THỊ MENU
    // ==========================================
    public int showMainMenuAndGetChoice() {
        // TODO: In tiêu đề (lấy từ StoreInfo.STORE_NAME), in các mục chọn:
        // 1. Quản lý sản phẩm (ProductManager)
        // 2. Nhập / Xuất kho theo lô (WarehouseManager)
        // 3. Thống kê & Báo cáo (StatisticsManager)
        // 0. Thoát
        // Gọi InputValidator để đọc và trả về lựa chọn
        return 0;
    }

    public int showProductSubMenu() {
        // TODO: Menu con: 1. Thêm SP, 2. Cập nhật SP, 3. Xóa SP, 4. Tìm kiếm SP, 5. Xem toàn bộ SP
        return 0;
    }

    public int showWarehouseSubMenu() {
        // TODO: Menu con: 1. Nhập lô hàng mới (Receive Stock), 2. Xuất hàng FIFO (Issue Stock)
        return 0;
    }

    public int showStatisticsSubMenu() {
        // TODO: Menu con: 1. Báo cáo tồn kho, 2. Kiểm tra lô hết hạn/sắp hết hàng, 3. Sắp xếp SP theo tồn kho, 4. Lịch sử giao dịch
        return 0;
    }

    // ==========================================
    // 2. NHÓM HÀM NHẬP FORM DỮ LIỆU TỪ NGƯỜI DÙNG
    // ==========================================
    public Product inputNewProductForm() {
        // TODO: Dùng InputValidator yêu cầu nhập barcode, name, category, unit, price
        // Khởi tạo và trả về đối tượng Product mới
        return null;
    }

    public Batch inputNewBatchForm(String barcode) {
        // TODO: Dùng InputValidator yêu cầu nhập batchId, quantity, importPrice, mfgDate, expDate
        // Khởi tạo và trả về đối tượng InventoryBatch mới
        return null;
    }

    public String inputBarcodePrompt(String actionName) {
        // TODO: Hiển thị lời nhắc nhập mã barcode cần tìm/xóa/nhập/xuất
        return null;
    }

    // ==========================================
    // 3. NHÓM HÀM HIỂN THỊ BẢNG & THÔNG BÁO
    // ==========================================
    public void printBatchHeader() {
        System.out.printf("%-10s | %8s | %-12s | %-12s%n", "BatchID", "Remaining", "MFG", "EXP");
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
        System.out.printf("%-10s | %8d | %-12s | %-12s%n",
                batch.getBatchId(),
                batch.getQuantity(),
                batch.getManufacturingDate(),
                batch.getExpiryDate());
    }

    public void printProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No product.");
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
        // TODO: In thông báo thành công
    }

    public void showErrorMessage(String error) {
        // TODO: In thông báo lỗi (ví dụ: không đủ tồn kho, trùng barcode...)
    }
}
