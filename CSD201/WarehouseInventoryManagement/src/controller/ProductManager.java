package controller;

import datastructure.ProductBST;
import java.util.List;
import model.Product;
import view.ConsoleView;

/**
 * Controller managing product operations using Binary Search Tree (BST).
 * 
 * @author Group4_SE2023
 */
public class ProductManager {

    private ProductBST productTree;

    /**
     * Default constructor initializing an empty ProductBST.
     */
    public ProductManager() {
        this.productTree = new ProductBST();
    }

    /**
     * Constructor injecting a shared ProductBST instance.
     *
     * @param productTree The shared product tree
     */
    public ProductManager(ProductBST productTree) {
        this.productTree = (productTree != null) ? productTree : new ProductBST();
    }

    public ProductBST getProductTree() {
        return productTree;
    }

    public void setProductTree(ProductBST productTree) {
        this.productTree = (productTree != null) ? productTree : new ProductBST();
    }

    /**
     * Adds a new product to the inventory system.
     * Checks for null and duplicate barcode before inserting into BST.
     *
     * @param product The product to add
     * @return true if added successfully, false if barcode duplicate or product invalid
     */
    public boolean addProduct(Product product) {
        if (product == null || product.getBarcode() == null || product.getBarcode().trim().isEmpty()) {
            return false;
        }

        String normalizedBarcode = product.getBarcode().trim();
        product.setBarcode(normalizedBarcode);

        // Check for duplicate barcode
        if (productTree.search(normalizedBarcode) != null) {
            return false;
        }

        return productTree.insert(product);
    }

    /**
     * Updates an existing product's details.
     *
     * @param barcode     The barcode of the product to update
     * @param newName     New product name (if non-empty)
     * @param newCategory New category (if non-empty)
     * @param newUnit     New unit (if non-empty)
     * @param newPrice    New price (if >= 0)
     * @return true if updated successfully, false if product not found
     */
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

    /**
     * Deletes a product by barcode.
     * Deletion is ONLY allowed if the product has 0 remaining stock across all batches.
     *
     * @param barcode The barcode of the product to delete
     * @return true if deleted successfully, false if not found or still has inventory
     */
    public boolean deleteProduct(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return false;
        }

        String normalizedBarcode = barcode.trim();
        Product existing = productTree.search(normalizedBarcode);
        if (existing == null) {
            return false;
        }

        // Business rule: Do not allow deleting a product that still has stock
        if (existing.calculateTotalQuantity() > 0) {
            return false;
        }

        return productTree.delete(normalizedBarcode);
    }

    /**
     * Searches for a product by barcode.
     *
     * @param barcode The barcode to look up
     * @return Product if found, null otherwise
     */
    public Product findProductByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }
        return productTree.search(barcode.trim());
    }

    /**
     * Checks if a product exists by barcode.
     *
     * @param barcode The barcode to check
     * @return true if exists, false otherwise
     */
    public boolean hasProduct(String barcode) {
        return findProductByBarcode(barcode) != null;
    }

    /**
     * Returns an in-order list of all products.
     *
     * @return List of products sorted by barcode
     */
    public List<Product> getAllProducts() {
        return productTree.inOrderList();
    }

    /**
     * Displays all products in the BST in alphabetical order of barcode.
     */
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
