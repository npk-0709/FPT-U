package controller;

import datastructure.ProductBST;
import model.Product;

/**
 *
 * @author tranvotiendo
 */
public class ProductManager {
    private ProductBST productTree;

    // TODO: Constructor nhận vào ProductBST dùng chung, Getters & Setters
    public boolean addProduct(Product product) {
        // TODO: Kiểm tra trùng barcode bằng search(), nếu chưa có thì gọi productTree.insert()
        return false;
    }

    public boolean updateProduct(String barcode, String newName, String newCategory, String newUnit, double newPrice) {
        // TODO: Tìm Product bằng search(), cập nhật các thuộc tính thông tin chung
        return false;
    }

    public boolean deleteProduct(String barcode) {
        // TODO: Tìm Product, kiểm tra nếu calculateTotalQuantity() == 0 thì mới cho xóa khỏi productTree
        return false;
    }

    public Product findProductByBarcode(String barcode) {
        // TODO: Gọi productTree.search(barcode)
        return null;
    }

    public void displayAllProducts() {
        // TODO: Gọi productTree.inOrderTraversal()
    }
}
