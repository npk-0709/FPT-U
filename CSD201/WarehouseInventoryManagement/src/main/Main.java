package main;

import handle.HandleProduct;
import handle.HandleStatistics;
import handle.HandleWarehouse;
import controller.ProductManager;
import controller.StatisticsManager;
import controller.WarehouseManager;
import datastructure.ProductBST;
import datastructure.TransactionLog;
import file.WarehouseFileService;
import util.InputValidator;
import view.ConsoleView;

public class Main {
    private static final ConsoleView view = new ConsoleView();
    private static final ProductBST productTree = new ProductBST();
    private static final TransactionLog transactionLog = new TransactionLog();
    private static final ProductManager productManager = new ProductManager(productTree);
    private static final WarehouseManager warehouseManager = new WarehouseManager(productTree, transactionLog);
    private static final WarehouseFileService fileService = new WarehouseFileService();
    private static final HandleProduct handleProduct = new HandleProduct(productManager, transactionLog, fileService);
    private static final HandleWarehouse handleWarehouse = new HandleWarehouse(productManager, warehouseManager, fileService);
    private static final HandleStatistics handleStatistics = new HandleStatistics(
            new StatisticsManager(productTree, transactionLog));

    public static void main(String[] args) {

        System.out.println(">> Automatically loading warehouse data from TXT files...");
        fileService.loadAll(productManager, transactionLog);

        boolean running = true;
        while (running) {
            int choice = view.showMainMenuAndGetChoice();
            switch (choice) {
                case 1:
                    handleProduct.handleProductMenu();
                    break;
                case 2:
                    handleWarehouse.handleWarehouseMenu();
                    break;
                case 3:
                    handleStatistics.handleStatisticsMenu();
                    break;
                case 0: {
                    if (fileService.isModified()) {
                        boolean save = InputValidator.readConfirmation("Data has been modified. Do you want to save changes to TXT files? (Y/N): ");
                        if (save) {
                            if (fileService.saveAll(productManager, transactionLog)) {
                                view.showSuccessMessage("Data saved successfully.");
                            } else {
                                view.showErrorMessage("Failed to save data to TXT files.");
                            }
                        }
                    }
                    view.showSuccessMessage("Goodbye!");
                    running = false;
                    break;
                }
            }
        }
    }
}
