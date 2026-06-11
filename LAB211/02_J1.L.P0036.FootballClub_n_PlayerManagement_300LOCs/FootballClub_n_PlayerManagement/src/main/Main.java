package main;

import manager.ClubManager;
import manager.PlayerManager;
import utils.ValidationUtils;


public class Main {

    private static final String CLUBS_FILE = "clubs.txt";
    private static final String PLAYERS_FILE = "players.txt";


    public static void main(String[] args) {
        ClubManager clubManager = new ClubManager(CLUBS_FILE);
        PlayerManager playerManager = new PlayerManager(PLAYERS_FILE, clubManager);
        System.out.println("=== Football Club & Player Management System ===");
        System.out.println("=== European Elite League (EEL) ===\n");
        clubManager.loadFromFile();
        playerManager.loadFromFile();

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = ValidationUtils.readMenuChoice("Enter your choice: ");
            switch (choice) {
                case 1:
                    clubManager.listClubs();
                    break;
                case 2:
                    clubManager.addClub();
                    break;
                case 3:
                    clubManager.searchClubById();
                    break;
                case 4:
                    clubManager.updateClubById();
                    break;
                case 5:
                    clubManager.filterClubsByBudget();
                    break;
                case 6:
                    playerManager.listPlayers();
                    break;
                case 7:
                    playerManager.searchPlayersByName();
                    break;
                case 8:
                    playerManager.addPlayer();
                    break;
                case 9:
                    playerManager.removePlayerById();
                    break;
                case 10:
                    playerManager.updatePlayerById();
                    break;
                case 11:
                    playerManager.listPlayersByPosition();
                    break;
                case 12:
                    clubManager.saveToFile();
                    playerManager.saveToFile();
                    break;
                case 13:
                    clubManager.loadFromFile();
                    playerManager.loadFromFile();
                    break;
                case 14:
                    if (clubManager.hasChanges() || playerManager.hasChanges()) {
                        System.out.println("Saving changes before exit...");
                        clubManager.saveToFile();
                        playerManager.saveToFile();
                    }
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please enter a number from 1 to 14.");
                    break;
            }
        }
    }


    private static void displayMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println(" 1. List clubs");
        System.out.println(" 2. Add club");
        System.out.println(" 3. Search club by ID");
        System.out.println(" 4. Update club by ID");
        System.out.println(" 5. Filter clubs by budget");
        System.out.println("-------------------------------");
        System.out.println(" 6. List players");
        System.out.println(" 7. Search players by name");
        System.out.println(" 8. Add player");
        System.out.println(" 9. Remove player by ID");
        System.out.println("10. Update player by ID");
        System.out.println("11. List players by position");
        System.out.println("-------------------------------");
        System.out.println("12. Save to files");
        System.out.println("13. Load from files");
        System.out.println("14. Quit");
        System.out.println("================================");
    }
}
