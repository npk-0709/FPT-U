package dispatcher;

import business.ClubsManager;
import business.PlayersManager;
import business.YouthPlayersManager;
import tools.Inputter;

/**
 * Lớp chính của chương trình — điều khiển menu 20 chức năng.
 * Tự động load dữ liệu khi khởi động.
 * Tự động save khi thoát nếu có thay đổi (dirty flag).
 */
public class Menu {

    private static final ClubsManager clubs = new ClubsManager();
    private static final PlayersManager players = new PlayersManager();
    private static final YouthPlayersManager youthPlayers = new YouthPlayersManager();

    /**
     * Hiển thị menu 20 chức năng.
     */
    private static void showMenu() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║      FOOTBALL CLUB & PLAYER MANAGEMENT (EEL)         ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  --- Club Management ---                             ║");
        System.out.println("║  1.  List all clubs                                  ║");
        System.out.println("║  2.  Add a new club                                  ║");
        System.out.println("║  3.  Search for a club by ID                         ║");
        System.out.println("║  4.  Update a club by ID                             ║");
        System.out.println("║  5.  List clubs with budget <= input value           ║");
        System.out.println("║  --- Player Management ---                           ║");
        System.out.println("║  6.  List players sorted by club name, shirt number  ║");
        System.out.println("║  7.  Search players by partial name                  ║");
        System.out.println("║  8.  Add a new player                                ║");
        System.out.println("║  9.  Remove a player by ID                           ║");
        System.out.println("║  10. Update a player by ID                           ║");
        System.out.println("║  11. List players by position                        ║");
        System.out.println("║  --- Youth Player Management ---                     ║");
        System.out.println("║  12. List all youth players                          ║");
        System.out.println("║  13. Add a new youth player                          ║");
        System.out.println("║  14. Update a youth player by ID                     ║");
        System.out.println("║  15. Remove a youth player by ID                     ║");
        System.out.println("║  16. List youth players eligible for first team      ║");
        System.out.println("║  --- System ---                                      ║");
        System.out.println("║  17. Save data to files                              ║");
        System.out.println("║  18. Load data from files                            ║");
        System.out.println("║  19. Quit                                            ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    /**
     * Function 18: Nạp lại dữ liệu từ file.
     * Xoá dữ liệu hiện tại, nạp club rồi player, rồi youth player, validate chặt.
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
        boolean youthOk = youthPlayers.loadStrict("youth_players.txt", clubs);
        if (!youthOk) {
            System.out.println("Load data failed!");
            return;
        }
        System.out.println("Load data successfully!");
    }

    /**
     * Function 19: Thoát chương trình.
     * Nếu có thay đổi (dirty) → tự động lưu trước khi thoát.
     */
    private static void quit() {
        if (clubs.isDirty() || players.isDirty() || youthPlayers.isDirty()) {
            System.out.println("Changes detected. Saving data before exit...");
            clubs.save();
            players.save();
            youthPlayers.save();
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
            choice = Inputter.inputInt("Choose 1-19: ", 1, 19);
            switch (choice) {
                // --- Club Management ---
                case 1:
                    clubs.listAll();
                    break;
                case 2:
                    clubs.add();
                    break;
                case 3:
                    clubs.searchById();
                    break;
                case 4:
                    clubs.update();
                    break;
                case 5:
                    clubs.listByBudget();
                    break;
                // --- Player Management ---
                case 6:
                    players.listSortedByClubThenShirt(clubs);
                    break;
                case 7:
                    players.searchByPartialName();
                    break;
                case 8:
                    players.add(clubs);
                    break;
                case 9:
                    players.remove();
                    break;
                case 10:
                    players.update(clubs);
                    break;
                case 11:
                    players.listByPosition();
                    break;
                // --- Youth Player Management ---
                case 12:
                    youthPlayers.listAll();
                    break;
                case 13:
                    youthPlayers.add(clubs);
                    break;
                case 14:
                    youthPlayers.update(clubs);
                    break;
                case 15:
                    youthPlayers.remove();
                    break;
                case 16:
                    youthPlayers.listEligibleForFirstTeam();
                    break;
                // --- System ---
                case 17:
                    clubs.save();
                    players.save();
                    youthPlayers.save();
                    break;
                case 18:
                    reloadAll();
                    break;
                case 19:
                    quit();
                    break;
            }
        } while (choice != 19);
    }
}
