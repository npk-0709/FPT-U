package controller;

import datastructure.ProductBST;
import datastructure.TransactionLog;
import model.Batch;

/**
 *
 * @author tranvotiendo
 */
public class WarehouseManager {

    private ProductBST productTree;
    private TransactionLog transactionLog;

    // TODO: Constructor nhận vào productTree và transactionLog dùng chung, Getters & Setters
    public boolean receiveStock(String barcode, Batch newBatch, String note) {
        // TODO:
        // 1. Tìm Product trong productTree.
        // 2. Thêm newBatch vào batchQueue của Product (enqueue).
        // 3. Tạo StockTransaction (INBOUND) và lưu vào transactionLog.
        return false;
    }

    public boolean issueStock(String barcode, int requestedQuantity, String note) {
        // TODO: (Entry #002 Part 1 - Xuất kho FIFO):
        // 1. Tìm Product và kiểm tra product.calculateTotalQuantity() >= requestedQuantity.
        // 2. Lặp qua batchQueue (dùng peek()):
        //    - Lấy lô cũ nhất ở đầu Queue, trừ số lượng (reduceQuantity).
        //    - Ghi lại StockTransaction (OUTBOUND) vào transactionLog cho lô vừa xuất.
        //    - Chỉ gọi dequeue() xóa lô khỏi Queue khi lô đó đã hết sạch hàng (quantity == 0).
        return false;
    }
}
