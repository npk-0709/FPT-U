
package handle;

import controller.StatisticsManager;
import view.ConsoleView;

public class HandleStatistics {
private ConsoleView view= new ConsoleView();
private StatisticsManager statisticsManager;

    public HandleStatistics() {
        this(new StatisticsManager());
    }

    public HandleStatistics(StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }

    public void handleStatisticsMenu() {
        boolean back = false;
        while (!back) {
            int choice = view.showStatisticsSubMenu();
            switch (choice) {
                case 1:
                    statisticsManager.generateInventorySummary();
                    break;
                case 2:
                    statisticsManager.checkExpiredOrLowStockBatches();
                    break;
                case 3:
                    statisticsManager.displaySortedProductsByQuantity();
                    break;
                case 4: {
                    String barcode = view.inputBarcodePrompt("view history (enter ALL to view all)");
                    statisticsManager.generateTransactionReport(barcode);
                    break;
                }
                case 0:
                    back = true;
                    break;
            }
        }
    }
}
