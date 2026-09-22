package dispatcher;

import java.util.ArrayList;
import java.util.List;

import business.AuthManager;
import business.ClubsManager;
import business.PlayersManager;
import business.YouthPlayersManager;
import model.Permission;
import model.User;
import tools.Inputter;

public class Menu {

    private final ClubsManager clubs = new ClubsManager();
    private final PlayersManager players = new PlayersManager();
    private final YouthPlayersManager youthPlayers = new YouthPlayersManager();
    private final AuthManager auth = new AuthManager();
    private final List<MenuItem> items = new ArrayList<>();

    private User currentUser;

    public static void main(String[] args) {
        new Menu().start();
    }

    private void start() {
        System.out.println("=== European Elite League (EEL) Management System ===");

        currentUser = auth.login();
        if (currentUser == null) {
            System.out.println("Too many failed attempts. Exiting.");
            return;
        }

        System.out.println("Loading data...");
        reloadAll();
        buildItems();
        run();
    }

    private void buildItems() {
        items.add(new MenuItem("List all clubs", Permission.CLUB_VIEW, clubs::listAll));
        items.add(new MenuItem("Add a new club", Permission.CLUB_MANAGE, clubs::add));
        items.add(new MenuItem("Search for a club by ID", Permission.CLUB_VIEW, clubs::searchById));
        items.add(new MenuItem("Update a club by ID", Permission.CLUB_MANAGE, clubs::update));
        items.add(new MenuItem("List clubs with budget <= input value", Permission.CLUB_VIEW, clubs::listByBudget));
        items.add(new MenuItem("List players sorted by club name, shirt number", Permission.PLAYER_VIEW, () -> players.listSortedByClubThenShirt(clubs)));
        items.add(new MenuItem("Search players by partial name", Permission.PLAYER_VIEW, players::searchByPartialName));
        items.add(new MenuItem("Add a new player", Permission.PLAYER_MANAGE, () -> players.add(clubs)));
        items.add(new MenuItem("Remove a player by ID", Permission.PLAYER_MANAGE, players::remove));
        items.add(new MenuItem("Update a player by ID", Permission.PLAYER_MANAGE, () -> players.update(clubs)));
        items.add(new MenuItem("List players by position", Permission.PLAYER_VIEW, players::listByPosition));
        items.add(new MenuItem("List all youth players", Permission.YOUTH_VIEW, youthPlayers::listAll));
        items.add(new MenuItem("Add a new youth player", Permission.YOUTH_MANAGE, () -> youthPlayers.add(clubs)));
        items.add(new MenuItem("Update a youth player by ID", Permission.YOUTH_MANAGE, () -> youthPlayers.update(clubs)));
        items.add(new MenuItem("Remove a youth player by ID", Permission.YOUTH_MANAGE, youthPlayers::remove));
        items.add(new MenuItem("List youth players eligible for first team", Permission.YOUTH_VIEW, youthPlayers::listEligibleForFirstTeam));
        items.add(new MenuItem("Save data to files", Permission.DATA_PERSIST, this::saveAll));
        items.add(new MenuItem("Load data from files", Permission.DATA_PERSIST, this::reloadAll));
    }

    private List<MenuItem> allowedItems() {
        List<MenuItem> allowed = new ArrayList<>();
        for (MenuItem item : items) {
            if (currentUser.can(item.getPermission())) {
                allowed.add(item);
            }
        }
        return allowed;
    }

    private void run() {
        List<MenuItem> allowed = allowedItems();
        int quitChoice = allowed.size() + 1;
        int choice;
        do {
            showMenu(allowed, quitChoice);
            choice = Inputter.inputInt("Choose 1-" + quitChoice + ": ", 1, quitChoice);
            if (choice == quitChoice) {
                quit();
            } else {
                allowed.get(choice - 1).run();
            }
        } while (choice != quitChoice);
    }

    private void showMenu(List<MenuItem> allowed, int quitChoice) {
        System.out.println();
        System.out.println("===== FOOTBALL CLUB & PLAYER MANAGEMENT (EEL) =====");
        System.out.println("Logged in as: " + currentUser.getUsername()
                + " [" + currentUser.getRole() + "]");
        System.out.println("---------------------------------------------------");
        for (int i = 0; i < allowed.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, allowed.get(i).getLabel());
        }
        System.out.printf("%2d. %s%n", quitChoice, "Quit");
        System.out.println("===================================================");
    }

    private void saveAll() {
        clubs.save();
        players.save();
        youthPlayers.save();
    }

    private void reloadAll() {
        if (!clubs.loadStrict("clubs.txt")) {
            System.out.println("Load data failed!");
            return;
        }
        if (!players.loadStrict("players.txt", clubs)) {
            System.out.println("Load data failed!");
            return;
        }
        if (!youthPlayers.loadStrict("youth_players.txt", clubs)) {
            System.out.println("Load data failed!");
            return;
        }
        System.out.println("Load data successfully!");
    }

    private void quit() {
        if (clubs.isDirty() || players.isDirty() || youthPlayers.isDirty()) {
            System.out.println("Changes detected. Saving data before exit...");
            saveAll();
        }
        System.out.println("Goodbye!");
    }
}
