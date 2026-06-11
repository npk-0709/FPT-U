package manager;

import model.Club;
import model.Player;
import utils.FileUtils;
import utils.ValidationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerManager {

    private List<Player> players;
    private final String filePath;
    private final ClubManager clubManager;
    private boolean hasChanges;

    public PlayerManager(String filePath, ClubManager clubManager) {
        this.filePath = filePath;
        this.clubManager = clubManager;
        this.players = new ArrayList<>();
        this.hasChanges = false;
    }

    public boolean hasChanges() {
        return hasChanges;
    }

    public void listPlayers() {
        if (players.isEmpty()) {
            System.out.println("No players found.");
            return;
        }

        List<Player> sorted = new ArrayList<>(players);
        sorted.sort(new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                String clubName1 = getClubName(p1.getClubId());
                String clubName2 = getClubName(p2.getClubId());
                int cmp = clubName1.compareToIgnoreCase(clubName2);
                if (cmp != 0) return cmp;
                return Integer.compare(p1.getShirtNumber(), p2.getShirtNumber());
            }
        });

        printPlayerTableHeader();
        for (Player player : sorted) {
            String clubName = getClubName(player.getClubId());
            System.out.printf("| %-10s | %-10s | %-20s | %-25s | %-12s | %6d |\n",
                    player.getPlayerId(), player.getClubId(), clubName,
                    player.getPlayerName(), player.getPosition(), player.getShirtNumber());
        }
        printPlayerTableFooter();
        System.out.println("Total: " + players.size() + " player(s).");
    }

    public void searchPlayersByName() {
        System.out.println("\n--- Search Players by Name ---");
        String keyword = ValidationUtils.readNonEmptyString("Enter player name to search: ");

        List<Player> results = new ArrayList<>();
        for (Player player : players) {
            if (player.getPlayerName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(player);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No players found matching '" + keyword + "'.");
        } else {
            printPlayerTableHeader();
            for (Player player : results) {
                String clubName = getClubName(player.getClubId());
                System.out.printf("| %-10s | %-10s | %-20s | %-25s | %-12s | %6d |\n",
                        player.getPlayerId(), player.getClubId(), clubName,
                        player.getPlayerName(), player.getPosition(), player.getShirtNumber());
            }
            printPlayerTableFooter();
            System.out.println("Found: " + results.size() + " player(s).");
        }
    }

    public void addPlayer() {
        System.out.println("\n--- Add New Player ---");

        String playerId;
        while (true) {
            playerId = ValidationUtils.readPatternString(
                    "Enter Player ID (Pxxxx): ",
                    "P\\d{4}",
                    "Error: Player ID must be in format Pxxxx (e.g., P0001)!"
            );
            if (findPlayerById(playerId) != null) {
                System.out.println("Error: Player ID '" + playerId + "' already exists!");
            } else {
                break;
            }
        }

        String clubId;
        while (true) {
            clubId = ValidationUtils.readNonEmptyString("Enter Club ID: ");
            if (clubManager.findClubById(clubId) == null) {
                System.out.println("Error: Club '" + clubId + "' does not exist!");
            } else {
                break;
            }
        }

        String playerName = ValidationUtils.readNonEmptyString("Enter Player Name: ");
        String position = ValidationUtils.readPosition("Enter Position (Goalkeeper/Defender/Midfielder/Forward/Winger): ");

        int shirtNumber;
        while (true) {
            shirtNumber = ValidationUtils.readIntInRange("Enter Shirt Number (1-99): ", 1, 99);
            if (isShirtNumberTaken(clubId, shirtNumber, null)) {
                System.out.println("Error: Shirt number " + shirtNumber + " is already taken in this club!");
            } else {
                break;
            }
        }

        Player player = new Player(playerId, clubId, playerName, position, shirtNumber);
        players.add(player);
        hasChanges = true;
        System.out.println("Player added successfully!");
    }

    public void removePlayerById() {
        System.out.println("\n--- Remove Player by ID ---");
        String playerId = ValidationUtils.readNonEmptyString("Enter Player ID to remove: ");
        Player player = findPlayerById(playerId);
        if (player == null) {
            System.out.println("Error: Player '" + playerId + "' does not exist!");
        } else {
            players.remove(player);
            hasChanges = true;
            System.out.println("Player '" + playerId + "' removed successfully!");
        }
    }

    public void updatePlayerById() {
        System.out.println("\n--- Update Player by ID ---");
        String playerId = ValidationUtils.readNonEmptyString("Enter Player ID to update: ");
        Player player = findPlayerById(playerId);
        if (player == null) {
            System.out.println("Error: Player '" + playerId + "' does not exist!");
            return;
        }

        System.out.println("Current info:");
        printPlayerTableHeader();
        String clubName = getClubName(player.getClubId());
        System.out.printf("| %-10s | %-10s | %-20s | %-25s | %-12s | %6d |\n",
                player.getPlayerId(), player.getClubId(), clubName,
                player.getPlayerName(), player.getPosition(), player.getShirtNumber());
        printPlayerTableFooter();

        System.out.println("(Press Enter to skip a field)");

        String newClubId = ValidationUtils.readOptionalString("New Club ID [" + player.getClubId() + "]: ");
        if (!newClubId.isEmpty()) {
            if (clubManager.findClubById(newClubId) == null) {
                System.out.println("Error: Club '" + newClubId + "' does not exist! Club ID not updated.");
            } else {
                player.setClubId(newClubId);
            }
        }

        String newName = ValidationUtils.readOptionalString("New Player Name [" + player.getPlayerName() + "]: ");
        if (!newName.isEmpty()) {
            player.setPlayerName(newName);
        }

        String newPos = ValidationUtils.readOptionalString("New Position [" + player.getPosition() + "]: ");
        if (!newPos.isEmpty()) {
            String[] validPositions = {"Goalkeeper", "Defender", "Midfielder", "Forward", "Winger"};
            boolean valid = false;
            for (String p : validPositions) {
                if (p.equalsIgnoreCase(newPos)) {
                    player.setPosition(p);
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                System.out.println("Error: Invalid position! Position not updated.");
            }
        }

        String shirtStr = ValidationUtils.readOptionalString("New Shirt Number [" + player.getShirtNumber() + "]: ");
        if (!shirtStr.isEmpty()) {
            try {
                int newShirt = Integer.parseInt(shirtStr);
                if (newShirt < 1 || newShirt > 99) {
                    System.out.println("Error: Shirt number must be 1-99! Shirt number not updated.");
                } else if (isShirtNumberTaken(player.getClubId(), newShirt, player.getPlayerId())) {
                    System.out.println("Error: Shirt number " + newShirt + " is already taken in this club! Not updated.");
                } else {
                    player.setShirtNumber(newShirt);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number! Shirt number not updated.");
            }
        }

        hasChanges = true;
        System.out.println("Player updated successfully!");
    }

    public void listPlayersByPosition() {
        System.out.println("\n--- List Players by Position ---");
        String position = ValidationUtils.readPosition("Enter Position (Goalkeeper/Defender/Midfielder/Forward/Winger): ");

        List<Player> results = new ArrayList<>();
        for (Player player : players) {
            if (player.getPosition().equalsIgnoreCase(position)) {
                results.add(player);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No players found for position: " + position);
        } else {
            printPlayerTableHeader();
            for (Player player : results) {
                String cn = getClubName(player.getClubId());
                System.out.printf("| %-10s | %-10s | %-20s | %-25s | %-12s | %6d |\n",
                        player.getPlayerId(), player.getClubId(), cn,
                        player.getPlayerName(), player.getPosition(), player.getShirtNumber());
            }
            printPlayerTableFooter();
            System.out.println("Found: " + results.size() + " player(s).");
        }
    }

    public void saveToFile() {
        FileUtils.writePlayers(filePath, players);
        hasChanges = false;
    }

    public void loadFromFile() {
        players = FileUtils.readPlayers(filePath, clubManager.getClubs());
        hasChanges = false;
        System.out.println("Loaded " + players.size() + " player(s) from file.");
    }

    private Player findPlayerById(String playerId) {
        for (Player player : players) {
            if (player.getPlayerId().equalsIgnoreCase(playerId)) {
                return player;
            }
        }
        return null;
    }

    private boolean isShirtNumberTaken(String clubId, int shirtNumber, String excludeId) {
        for (Player player : players) {
            if (player.getClubId().equalsIgnoreCase(clubId)
                    && player.getShirtNumber() == shirtNumber
                    && (excludeId == null || !player.getPlayerId().equalsIgnoreCase(excludeId))) {
                return true;
            }
        }
        return false;
    }

    private String getClubName(String clubId) {
        Club club = clubManager.findClubById(clubId);
        return (club != null) ? club.getClubName() : "Unknown";
    }

    private void printPlayerTableHeader() {
        System.out.println("+------------+------------+----------------------+---------------------------+--------------+--------+");
        System.out.printf("| %-10s | %-10s | %-20s | %-25s | %-12s | %6s |\n",
                "Player ID", "Club ID", "Club Name", "Player Name", "Position", "Shirt#");
        System.out.println("+------------+------------+----------------------+---------------------------+--------------+--------+");
    }

    private void printPlayerTableFooter() {
        System.out.println("+------------+------------+----------------------+---------------------------+--------------+--------+");
    }
}
