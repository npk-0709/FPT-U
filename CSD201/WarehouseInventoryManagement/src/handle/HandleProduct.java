package handle;

import java.util.ArrayList;
import java.util.List;
import controller.ProductManager;
import datastructure.TransactionLog;
import file.WarehouseFileService;
import model.Product;
import util.InputValidator;
import view.ConsoleView;

public class HandleProduct {

    private ConsoleView view = new ConsoleView();
    private ProductManager productManager;
    private TransactionLog transactionLog;
    private WarehouseFileService fileService;

    public HandleProduct() {
        this(new ProductManager(), null, new WarehouseFileService());
    }

    public HandleProduct(ProductManager productManager) {
        this(productManager, null, new WarehouseFileService());
    }

    public HandleProduct(ProductManager productManager, TransactionLog transactionLog, WarehouseFileService fileService) {
        this.productManager = productManager;
        this.transactionLog = transactionLog;
        this.fileService = fileService;
    }

    public void setTransactionLog(TransactionLog transactionLog) {
        this.transactionLog = transactionLog;
    }

    public void setFileService(WarehouseFileService fileService) {
        this.fileService = fileService;
    }

    public void handleProductMenu() {
        boolean back = false;
        while (!back) {
            int choice = view.showProductSubMenu();
            switch (choice) {
                case 1: {
                    Product p = view.inputNewProductForm();
                    if (productManager.addProduct(p)) {
                        if (fileService != null) {
                            fileService.markModified();
                        }
                        view.showSuccessMessage("Product added.");
                    } else {
                        view.showErrorMessage("Barcode already exists!");
                    }
                    break;
                }
                case 2: {
                    String barcode = view.inputBarcodePrompt("update");
                    Product existing = productManager.findProductByBarcode(barcode);
                    if (existing == null) {
                        view.showErrorMessage("Product not found.");
                    } else {
                        List<Product> listProduct = new ArrayList<>();
                        listProduct.add(existing);
                        view.printProducts(listProduct);

                        System.out.println("(Press Enter to keep the current value)");
                        String newName = InputValidator.readString("Enter new name: ");
                        String newCategory = InputValidator.readString("Enter new category: ");
                        String newUnit = InputValidator.readString("Enter new unit: ");

                        double newPrice = -1;
                        String priceInput = InputValidator.readString("Enter new price: ");
                        if (!priceInput.isEmpty()) {
                            try {
                                double parsed = Double.parseDouble(priceInput);
                                if (parsed > 0) {
                                    newPrice = parsed;
                                } else {
                                    view.showErrorMessage("Price must be > 0. Keeping the current price.");
                                }
                            } catch (NumberFormatException e) {
                                view.showErrorMessage("Invalid price. Keeping the current price.");
                            }
                        }

                        if (productManager.updateProduct(barcode, newName, newCategory, newUnit, newPrice)) {
                            if (fileService != null) {
                                fileService.markModified();
                            }
                            view.showSuccessMessage("Product updated.");
                        } else {
                            view.showErrorMessage("Update failed.");
                        }
                    }
                    break;
                }
                case 3: {
                    String barcode = view.inputBarcodePrompt("delete");
                    Product deleteProduct = productManager.findProductByBarcode(barcode);
                    if (deleteProduct != null) {
                        if (deleteProduct.calculateTotalQuantity() > 0) {
                            view.showErrorMessage("Cannot delete: Product still has " + deleteProduct.calculateTotalQuantity() + " items in stock.");
                        } else if (productManager.deleteProduct(barcode)) {
                            if (fileService != null) {
                                fileService.markModified();
                            }
                            view.showSuccessMessage("Product deleted.");
                        } else {
                            view.showErrorMessage("Delete failed.");
                        }
                    } else {
                        view.showErrorMessage("Product not found.");
                    }
                    break;
                }
                case 4: {
                    String barcode = view.inputBarcodePrompt("search");
                    Product product = productManager.findProductByBarcode(barcode);

                    if (product == null) {
                        view.showErrorMessage("Product not found.");
                    } else {
                        List<Product> listProduct = new ArrayList<>();
                        listProduct.add(product);
                        view.printProducts(listProduct);
                        view.printBatches(product.getBatchQueue());
                    }
                    break;
                }
                case 5:
                    view.printProducts(productManager.getAllProducts());
                    break;
                case 6: {
                    if (fileService != null && fileService.saveAll(productManager, transactionLog)) {
                        view.showSuccessMessage("Data saved to TXT files (products.txt, batches.txt, transactions.txt).");
                    } else {
                        view.showErrorMessage("Failed to save data to TXT files.");
                    }
                    break;
                }
                case 7: {
                    if (fileService != null && fileService.loadAll(productManager, transactionLog)) {
                        view.showSuccessMessage("Data loaded from TXT files.");
                    } else {
                        view.showErrorMessage("Failed to load data from TXT files.");
                    }
                    break;
                }
                case 0:
                    back = true;
                    break;
            }
        }
    }

}
