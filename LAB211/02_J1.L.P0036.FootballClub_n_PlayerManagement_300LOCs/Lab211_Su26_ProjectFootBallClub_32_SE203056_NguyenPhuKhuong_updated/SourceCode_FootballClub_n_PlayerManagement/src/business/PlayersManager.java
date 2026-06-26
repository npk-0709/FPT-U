package business;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.Player;
import model.Validatable;
import tools.Inputter;

public class PlayersManager {

    private List<Player> list = new ArrayList<>();
    private boolean dirty = false;
    private static final String FILE_PATH = "players.txt";

    public void listSortedByClubThenShirt(ClubsManager clubs) {
        if (list.isEmpty()) {
            System.out.println("The player list is empty.");
            return;
        }
        List<Player> copy = new ArrayList<>(list);

        Comparator<Player> byClubName = Comparator.comparing(
                p -> clubs.getName(p.getClubID()),
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
        );
        copy.sort(byClubName.thenComparingInt(Player::getShirtNumber));

        printHeader();
        copy.forEach(System.out::println);
    }

    public void searchByPartialName() {
        String kw = Inputter.inputNonEmpty("Partial player name: ").toLowerCase();
        List<Player> found = new ArrayList<>();
        for (Player p : list) {
            if (p.getName().toLowerCase().contains(kw)) {
                found.add(p);
            }
        }
        if (found.isEmpty()) {
            System.out.println("No players match the search criteria!");
        } else {
            printHeader();
            found.forEach(System.out::println);
        }
    }

    public void add(ClubsManager clubs) {
        String id = Inputter.inputLoop("Player ID: ",
                Validatable.PLAYER_ID_REGEX, "Format must be Pxxxx");
        if (findById(id) != null) {
            System.out.println("This player ID already exists!");
            return;
        }

        System.out.println("\n--- Available Clubs ---");
        clubs.listAll();
        System.out.println();

        String clubId = Inputter.inputLoop("Club ID: ",
                Validatable.CLUB_ID_REGEX, "Format must be CL-xxxx");
        if (clubs.findById(clubId) == null) {
            System.out.println("This club does not exist!");
            return;
        }

        String name = Inputter.inputNonEmpty("Player name: ");

        String position;
        while (true) {
            position = Inputter.inputNonEmpty(
                    "Position (Goalkeeper/Defender/Midfielder/Forward/Winger): ");
            if (Validatable.isPosition(position)) break;
            System.out.println("Invalid position! Must be: Goalkeeper, Defender, Midfielder, Forward, Winger.");
        }
        position = position.substring(0, 1).toUpperCase()
                + position.substring(1).toLowerCase();

        int shirt = Inputter.inputInt("Shirt number (1-99): ", 1, 99);
        if (shirtTakenInClub(clubId, shirt, null)) {
            System.out.println("This shirt number already exists in this club!");
            return;
        }

        list.add(new Player(id, clubId, name, position, shirt));
        dirty = true;
        System.out.println("Player added successfully.");
    }

    public void remove() {
        String id = Inputter.inputLoop("Player ID: ",
                Validatable.PLAYER_ID_REGEX, "Format must be Pxxxx");
        Player p = findById(id);
        if (p == null) {
            System.out.println("This player does not exist!");
            return;
        }
        list.remove(p);
        dirty = true;
        System.out.println("Player removed successfully.");
    }

    public void update(ClubsManager clubs) {
        String id = Inputter.inputLoop("Player ID: ",
                Validatable.PLAYER_ID_REGEX, "Format must be Pxxxx");
        Player p = findById(id);
        if (p == null) {
            System.out.println("This player does not exist!");
            return;
        }

        String name = Inputter.inputOptional(
                "Name [" + p.getName() + "] (Enter to skip): ");
        if (!name.isEmpty()) p.setName(name);

        String pos = Inputter.inputOptional(
                "Position [" + p.getPosition() + "] (Enter to skip): ");
        if (!pos.isEmpty()) {
            if (Validatable.isPosition(pos)) {
                pos = pos.substring(0, 1).toUpperCase()
                        + pos.substring(1).toLowerCase();
                p.setPosition(pos);
            } else {
                System.out.println("Invalid position! Keeping current value.");
            }
        }

        String shirtStr = Inputter.inputOptional(
                "Shirt number [" + p.getShirtNumber() + "] (Enter to skip): ");
        if (!shirtStr.isEmpty()) {
            try {
                int shirt = Integer.parseInt(shirtStr);
                if (shirt >= 1 && shirt <= 99) {
                    if (!shirtTakenInClub(p.getClubID(), shirt, p)) {
                        p.setShirtNumber(shirt);
                    } else {
                        System.out.println("This shirt number already exists in this club!");
                    }
                } else {
                    System.out.println("Shirt number must be between 1 and 99. Keeping current value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current value.");
            }
        }

        dirty = true;
        System.out.println("Player updated successfully.");
    }

    public void listByPosition() {
        String pos = Inputter.inputNonEmpty("Position: ");
        printHeader();
        boolean any = false;
        for (Player p : list) {
            if (p.getPosition().equalsIgnoreCase(pos)) {
                System.out.println(p);
                any = true;
            }
        }
        if (!any) System.out.println("No players play in this position.");
    }

    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Player p : list) {
                pw.printf("%s, %s, %s, %s, %d%n",
                        p.getId(), p.getClubID(), p.getName(),
                        p.getPosition(), p.getShirtNumber());
            }
            dirty = false;
            System.out.println("Player data saved to players.txt.");
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    public boolean loadStrict(String path, ClubsManager clubs) {
        List<Player> tmp = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\s*,\\s*");
                if (parts.length != 5) return false;
                if (!Validatable.isValid(parts[0], Validatable.PLAYER_ID_REGEX))
                    return false;
                if (!Validatable.isValid(parts[1], Validatable.CLUB_ID_REGEX))
                    return false;
                if (clubs.findById(parts[1]) == null) return false;
                if (parts[2].isEmpty()) return false;
                if (!Validatable.isPosition(parts[3])) return false;
                int shirt;
                try {
                    shirt = Integer.parseInt(parts[4]);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (shirt < 1 || shirt > 99) return false;
                for (Player existing : tmp) {
                    if (existing.getId().equals(parts[0])) return false;
                }
                for (Player existing : tmp) {
                    if (existing.getClubID().equals(parts[1])
                            && existing.getShirtNumber() == shirt) {
                        return false;
                    }
                }
                tmp.add(new Player(parts[0], parts[1], parts[2], parts[3], shirt));
            }
        } catch (Exception e) {
            return false;
        }
        list = tmp;
        dirty = false;
        return true;
    }

    public void listAll() {
        if (list.isEmpty()) {
            System.out.println("The player list is empty.");
            return;
        }
        printHeader();
        list.forEach(System.out::println);
    }

    public Player findById(String id) {
        for (Player p : list) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    private boolean shirtTakenInClub(String clubId, int shirt, Player except) {
        for (Player p : list) {
            if (p != except
                    && p.getClubID().equals(clubId)
                    && p.getShirtNumber() == shirt) {
                return true;
            }
        }
        return false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public List<Player> getList() {
        return list;
    }

    private void printHeader() {
        System.out.printf("%-7s| %-10s| %-22s| %-12s| %s%n",
                "ID", "Club ID", "Player Name", "Position", "Shirt");
        System.out.println("--------------------------------------------------------------");
    }
}
