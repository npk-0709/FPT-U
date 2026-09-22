package business;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import model.YouthPlayer;
import model.Validatable;
import tools.Inputter;

public class YouthPlayersManager {

    private List<YouthPlayer> list = new ArrayList<>();
    private boolean dirty = false;
    private static final String FILE_PATH = "youth_players.txt";

    public void listAll() {
        if (list.isEmpty()) {
            System.out.println("The youth player list is empty.");
            return;
        }
        List<YouthPlayer> copy = new ArrayList<>(list);
        copy.sort(null);
        printHeader();
        for (YouthPlayer yp : copy) {
            System.out.println(yp);
        }
        System.out.println("--------------------------------------------------------------");
        System.out.println("Total: " + list.size() + " youth player(s).");
    }

    public void add(ClubsManager clubs) {
        String id = Inputter.inputLoop("Youth Player ID: ",
                Validatable.YOUTH_PLAYER_ID_REGEX, "Format must be AC-xxxx");
        if (findById(id) != null) {
            System.out.println("This youth player ID already exists!");
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

        String name = Inputter.inputNonEmpty("Youth player name: ");

        int age = Inputter.inputInt("Age (" + Validatable.YOUTH_MIN_AGE
                + "-" + Validatable.YOUTH_MAX_AGE + "): ",
                Validatable.YOUTH_MIN_AGE, Validatable.YOUTH_MAX_AGE);

        YouthPlayer yp = new YouthPlayer(id, clubId, name, age);
        list.add(yp);
        dirty = true;
        System.out.println("Youth player added successfully.");

        checkAndSuggestPromotion(yp);
    }

    public void update(ClubsManager clubs) {
        String id = Inputter.inputLoop("Youth Player ID: ",
                Validatable.YOUTH_PLAYER_ID_REGEX, "Format must be AC-xxxx");
        YouthPlayer yp = findById(id);
        if (yp == null) {
            System.out.println("This youth player does not exist!");
            return;
        }

        System.out.println("Current info: " + yp.getDisplayInfo());

        String name = Inputter.inputOptional(
                "Name [" + yp.getName() + "] (Enter to skip): ");
        if (!name.isEmpty()) yp.setName(name);

        String ageStr = Inputter.inputOptional(
                "Age [" + yp.getAge() + "] (Enter to skip): ");
        if (!ageStr.isEmpty()) {
            try {
                int age = Integer.parseInt(ageStr);
                if (age >= Validatable.YOUTH_MIN_AGE && age <= Validatable.YOUTH_MAX_AGE) {
                    yp.setAge(age);
                } else {
                    System.out.println("Age must be between " + Validatable.YOUTH_MIN_AGE
                            + " and " + Validatable.YOUTH_MAX_AGE + ". Keeping current value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current value.");
            }
        }

        dirty = true;
        System.out.println("Youth player updated successfully.");

        checkAndSuggestPromotion(yp);
    }

    public void remove() {
        String id = Inputter.inputLoop("Youth Player ID: ",
                Validatable.YOUTH_PLAYER_ID_REGEX, "Format must be AC-xxxx");
        YouthPlayer yp = findById(id);
        if (yp == null) {
            System.out.println("This youth player does not exist!");
            return;
        }

        System.out.println("Youth player to delete: " + yp.getDisplayInfo());

        boolean confirm = Inputter.inputYesNo("Are you sure you want to delete? (Y/N): ");
        if (confirm) {
            list.remove(yp);
            dirty = true;
            System.out.println("Youth player removed successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    public void listEligibleForFirstTeam() {
        if (list.isEmpty()) {
            System.out.println("The youth player list is empty.");
            return;
        }

        List<YouthPlayer> eligible = new ArrayList<>();
        for (YouthPlayer yp : list) {
            if (yp.isEligibleForFirstTeam()) {
                eligible.add(yp);
            }
        }

        if (eligible.isEmpty()) {
            System.out.println("No youth players are currently eligible for the first team (age >= "
                    + Validatable.FIRST_TEAM_AGE + ").");
            return;
        }

        eligible.sort(null);
        System.out.println("=== Youth Players Eligible for First Team (age >= "
                + Validatable.FIRST_TEAM_AGE + ") ===");
        printHeader();
        for (YouthPlayer yp : eligible) {
            System.out.println(yp);
        }
        System.out.println("--------------------------------------------------------------");
        System.out.println("Total eligible: " + eligible.size() + " youth player(s).");
    }

    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (YouthPlayer yp : list) {
                pw.printf("%s, %s, %s, %d%n",
                        yp.getId(), yp.getClubId(), yp.getName(), yp.getAge());
            }
            dirty = false;
            System.out.println("Youth player data saved to youth_players.txt.");
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    public boolean loadStrict(String path, ClubsManager clubs) {
        List<YouthPlayer> tmp = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\s*,\\s*");
                if (parts.length != 4) return false;
                if (!Validatable.isValid(parts[0], Validatable.YOUTH_PLAYER_ID_REGEX))
                    return false;
                if (!Validatable.isValid(parts[1], Validatable.CLUB_ID_REGEX))
                    return false;
                if (clubs.findById(parts[1]) == null) return false;
                if (parts[2].isEmpty()) return false;
                int age;
                try {
                    age = Integer.parseInt(parts[3]);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (age < Validatable.YOUTH_MIN_AGE || age > Validatable.YOUTH_MAX_AGE)
                    return false;
                for (YouthPlayer existing : tmp) {
                    if (existing.getId().equals(parts[0])) return false;
                }
                tmp.add(new YouthPlayer(parts[0], parts[1], parts[2], age));
            }
        } catch (FileNotFoundException e) {
            list = new ArrayList<>();
            dirty = false;
            return true;
        } catch (Exception e) {
            return false;
        }
        list = tmp;
        dirty = false;
        return true;
    }

    public YouthPlayer findById(String id) {
        for (YouthPlayer yp : list) {
            if (yp.getId().equalsIgnoreCase(id)) return yp;
        }
        return null;
    }

    public boolean isDirty() {
        return dirty;
    }

    public List<YouthPlayer> getList() {
        return list;
    }

    private void checkAndSuggestPromotion(YouthPlayer yp) {
        if (yp.isEligibleForFirstTeam()) {
            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║  ★ PROMOTION SUGGESTION                              ║");
            System.out.println("║  Youth player: " + padRight(yp.getName(), 38) + "║");
            System.out.println("║  Age: " + padRight(String.valueOf(yp.getAge()), 47) + "║");
            System.out.println("║  → Eligible to be promoted to the FIRST TEAM!        ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
        }
    }

    private void printHeader() {
        System.out.printf("%-8s| %-10s| %-22s| %-4s| %s%n",
                "ID", "Club ID", "Player Name", "Age", "First Team?");
        System.out.println("--------------------------------------------------------------");
    }

    private String padRight(String s, int n) {
        if (s.length() >= n) return s.substring(0, n);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < n) sb.append(' ');
        return sb.toString();
    }
}
