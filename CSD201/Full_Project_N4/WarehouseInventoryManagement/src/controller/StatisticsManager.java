package controller;

import datastructure.ProductBST;
import datastructure.TransactionLog;

/**
 *
 * @author tranvotiendo
 */
public class StatisticsManager {

    private ProductBST productTree;
    private TransactionLog transactionLog;

    // TODO: Constructor nhận vào productTree và transactionLog gốc, Getters & Setters
    public void generateInventorySummary() {
        // TODO: Đọc trực tiếp từ productTree để thống kê số lượng tồn của từng sản phẩm
    }

    public void checkExpiredOrLowStockBatches() {
        // TODO: Duyệt qua các sản phẩm và kiểm tra các lô hàng sắp hết hạn/đã hết hạn hoặc tồn kho thấp
    }

    public void displaySortedProductsByQuantity() {
        // TODO: Trích xuất danh sách sản phẩm từ productTree và sắp xếp theo tổng số lượng tồn kho
    }

    public void generateTransactionReport(String barcode) {
        // TODO: Đọc từ transactionLog để in báo cáo tổng nhập/xuất và lịch sử của sản phẩm
    }
}
