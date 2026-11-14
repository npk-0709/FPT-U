package controller;

import datastructure.ProductBST;
import java.util.List;
import model.Product;
import view.ConsoleView;

public class ProductManager {

    private ProductBST productTree;

    public ProductManager() {
        this.productTree = new ProductBST();
    }

    public ProductManager(ProductBST productTree) {
        this.productTree = (productTree != null) ? productTree : new ProductBST();
    }

    public ProductBST getProductTree() {
        return productTree;
    }

    public void setProductTree(ProductBST productTree) {
        this.productTree = (productTree != null) ? productTree : new ProductBST();
    }

    public void clear() {
        if (productTree != null) {
            productTree.clear();
        }
    }

    public boolean addProduct(Product product) {
        if (product == null || product.getBarcode() == null || product.getBarcode().trim().isEmpty()) {
            return false;
        }

        String normalizedBarcode = product.getBarcode().trim();
        product.setBarcode(normalizedBarcode);

        if (productTree.search(normalizedBarcode) != null) {
            return false;
        }

        return productTree.insert(product);
    }

    public boolean updateProduct(String barcode, String newName, String newCategory, String newUnit, double newPrice) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return false;
        }

        Product existing = productTree.search(barcode.trim());
        if (existing == null) {
            return false;
        }

        if (newName != null && !newName.trim().isEmpty()) {
            existing.setName(newName.trim());
        }
        if (newCategory != null && !newCategory.trim().isEmpty()) {
            existing.setCategory(newCategory.trim());
        }
        if (newUnit != null && !newUnit.trim().isEmpty()) {
            existing.setUnit(newUnit.trim());
        }
        if (newPrice >= 0) {
            existing.setPrice(newPrice);
        }

        return true;
    }

    public boolean deleteProduct(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return false;
        }

        String normalizedBarcode = barcode.trim();
        Product existing = productTree.search(normalizedBarcode);
        if (existing == null) {
            return false;
        }

        if (existing.calculateTotalQuantity() > 0) {
            return false;
        }

        return productTree.delete(normalizedBarcode);
    }

    public Product findProductByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }
        return productTree.search(barcode.trim());
    }

    public boolean hasProduct(String barcode) {
        return findProductByBarcode(barcode) != null;
    }

    public List<Product> getAllProducts() {
        return productTree.inOrderList();
    }

    public void displayAllProducts() {
        List<Product> products = productTree.inOrderList();
        if (products == null || products.isEmpty()) {
            System.out.println(">> Notice: Currently there are no products in the system.");
            return;
        }
        ConsoleView view = new ConsoleView();
        view.printProducts(products);
    }
}
