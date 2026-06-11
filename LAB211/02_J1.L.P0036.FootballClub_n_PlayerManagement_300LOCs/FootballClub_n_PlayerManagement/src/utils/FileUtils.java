package utils;

import model.Club;
import model.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<Club> readClubs(String filePath) {
        List<Club> clubs = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return clubs;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    String[] parts = line.split(",");
                    if (parts.length != 4) {
                        throw new IllegalArgumentException("Invalid number of fields");
                    }
                    String clubId = parts[0].trim();
                    String clubName = parts[1].trim();
                    String sponsorBrand = parts[2].trim();
                    double budget = Double.parseDouble(parts[3].trim());

                    if (!clubId.matches("CL-\\d{4}")) {
                        throw new IllegalArgumentException("Invalid clubId format: " + clubId);
                    }
                    if (clubName.isEmpty() || sponsorBrand.isEmpty()) {
                        throw new IllegalArgumentException("Club name or sponsor brand is empty");
                    }
                    if (budget <= 0) {
                        throw new IllegalArgumentException("Budget must be positive");
                    }

                    clubs.add(new Club(clubId, clubName, sponsorBrand, budget));
                } catch (Exception e) {
                    System.out.println("Load data failed! (Line " + lineNumber + ": " + e.getMessage() + ")");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return clubs;
    }

    public static List<Player> readPlayers(String filePath, List<Club> clubs) {
        List<Player> players = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return players;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    String[] parts = line.split(",");
                    if (parts.length != 5) {
                        throw new IllegalArgumentException("Invalid number of fields");
                    }
                    String playerId = parts[0].trim();
                    String clubId = parts[1].trim();
                    String playerName = parts[2].trim();
                    String position = parts[3].trim();
                    int shirtNumber = Integer.parseInt(parts[4].trim());

                    if (!playerId.matches("P\\d{4}")) {
                        throw new IllegalArgumentException("Invalid playerId format: " + playerId);
                    }
                    boolean clubExists = false;
                    for (Club c : clubs) {
                        if (c.getClubId().equals(clubId)) {
                            clubExists = true;
                            break;
                        }
                    }
                    if (!clubExists) {
                        throw new IllegalArgumentException("Club " + clubId + " does not exist");
                    }
                    if (playerName.isEmpty()) {
                        throw new IllegalArgumentException("Player name is empty");
                    }
                    String[] validPositions = {"Goalkeeper", "Defender", "Midfielder", "Forward", "Winger"};
                    boolean posValid = false;
                    for (String p : validPositions) {
                        if (p.equalsIgnoreCase(position)) {
                            position = p;
                            posValid = true;
                            break;
                        }
                    }
                    if (!posValid) {
                        throw new IllegalArgumentException("Invalid position: " + position);
                    }
                    if (shirtNumber < 1 || shirtNumber > 99) {
                        throw new IllegalArgumentException("Shirt number must be 1-99");
                    }
                    boolean duplicate = false;
                    for (Player existing : players) {
                        if (existing.getClubId().equals(clubId) && existing.getShirtNumber() == shirtNumber) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (duplicate) {
                        throw new IllegalArgumentException("Shirt number " + shirtNumber + " already exists in club " + clubId);
                    }

                    players.add(new Player(playerId, clubId, playerName, position, shirtNumber));
                } catch (NumberFormatException e) {
                    System.out.println("Load data failed! (Line " + lineNumber + ": Invalid number format)");
                } catch (Exception e) {
                    System.out.println("Load data failed! (Line " + lineNumber + ": " + e.getMessage() + ")");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return players;
    }

    public static void writeClubs(String filePath, List<Club> clubs) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Club club : clubs) {
                pw.println(club.toString());
            }
            System.out.println("Clubs saved successfully to " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    public static void writePlayers(String filePath, List<Player> players) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Player player : players) {
                pw.println(player.toString());
            }
            System.out.println("Players saved successfully to " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}
