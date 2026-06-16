package dispatcher;

import business.ClubsManager;
import business.PlayersManager;
import tools.Inputter;

/**
 * Lớp chính của chương trình — điều khiển menu 14 chức năng.
 * Tự động load dữ liệu khi khởi động.
 * Tự động save khi thoát nếu có thay đổi (dirty flag).
 */
public class Menu {

    private static final ClubsManager clubs   = new ClubsManager();
    private static final PlayersManager players = new PlayersManager();

    /**
     * Hiển thị menu 14 chức năng.
     */
    private static void showMenu() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║      FOOTBALL CLUB & PLAYER MANAGEMENT (EEL)        ║");
        System.out.println("╠═══════════════════════════════════════════════════════╣");
        System.out.println("║  1.  List all clubs                                  ║");
        System.out.println("║  2.  Add a new club                                  ║");
        System.out.println("║  3.  Search for a club by ID                         ║");
        System.out.println("║  4.  Update a club by ID                             ║");
        System.out.println("║  5.  List clubs with budget <= input value            ║");
        System.out.println("║  6.  List players sorted by club name, shirt number  ║");
        System.out.println("║  7.  Search players by partial name                  ║");
        System.out.println("║  8.  Add a new player                                ║");
        System.out.println("║  9.  Remove a player by ID                           ║");
        System.out.println("║  10. Update a player by ID                           ║");
        System.out.println("║  11. List players by position                        ║");
        System.out.println("║  12. Save data to files                              ║");
        System.out.println("║  13. Load data from files                            ║");
        System.out.println("║  14. Quit                                            ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
    }

    /**
     * Function 13: Nạp lại dữ liệu từ file.
     * Xoá dữ liệu hiện tại, nạp club rồi player, validate chặt.
     */
    private static void reloadAll() {
        boolean clubOk = clubs.loadStrict("clubs.txt");
        if (!clubOk) {
            System.out.println("Load data failed!");
            return;
        }
        boolean playerOk = players.loadStrict("players.txt", clubs);
        if (!playerOk) {
            System.out.println("Load data failed!");
            return;
        }
        System.out.println("Load data successfully!");
    }

    /**
     * Function 14: Thoát chương trình.
     * Nếu có thay đổi (dirty) → tự động lưu trước khi thoát.
     */
    private static void quit() {
        if (clubs.isDirty() || players.isDirty()) {
            System.out.println("Changes detected. Saving data before exit...");
            clubs.save();
            players.save();
        }
        System.out.println("Goodbye!");
    }

    /**
     * Hàm main — entry point của chương trình.
     * Tự động load dữ liệu khi khởi động,
     * sau đó chạy vòng lặp menu cho đến khi chọn Quit.
     */
    public static void main(String[] args) {
        System.out.println("=== European Elite League (EEL) Management System ===");
        System.out.println("Loading data...");

        // Auto-load dữ liệu khi khởi động
        reloadAll();

        int choice;
        do {
            showMenu();
            choice = Inputter.inputInt("Choose 1-14: ", 1, 14);
            switch (choice) {
                case 1  -> clubs.listAll();
                case 2  -> clubs.add();
                case 3  -> clubs.searchById();
                case 4  -> clubs.update();
                case 5  -> clubs.listByBudget();
                case 6  -> players.listSortedByClubThenShirt(clubs);
                case 7  -> players.searchByPartialName();
                case 8  -> players.add(clubs);
                case 9  -> players.remove();
                case 10 -> players.update(clubs);
                case 11 -> players.listByPosition();
                case 12 -> { clubs.save(); players.save(); }
                case 13 -> reloadAll();
                case 14 -> quit();
            }
        } while (choice != 14);
    }
}
