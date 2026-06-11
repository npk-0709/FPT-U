package manager;

import model.Club;
import utils.FileUtils;
import utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;


public class ClubManager {

    private List<Club> clubs;
    private final String filePath;
    private boolean hasChanges;


    public ClubManager(String filePath) {
        this.filePath = filePath;
        this.clubs = new ArrayList<>();
        this.hasChanges = false;
    }


    public List<Club> getClubs() {
        return clubs;
    }


    public boolean hasChanges() {
        return hasChanges;
    }


    public void listClubs() {
        if (clubs.isEmpty()) {
            System.out.println("No clubs found.");
            return;
        }
        printClubTableHeader();
        for (Club club : clubs) {
            club.displayInfo();
        }
        printClubTableFooter();
        System.out.println("Total: " + clubs.size() + " club(s).");
    }

    public void addClub() {
        System.out.println("\n--- Add New Club ---");

        String clubId;
        while (true) {
            clubId = ValidationUtils.readPatternString(
                    "Enter Club ID (CL-xxxx): ",
                    "CL-\\d{4}",
                    "Error: Club ID must be in format CL-xxxx (e.g., CL-0001)!"
            );
            if (findClubById(clubId) != null) {
                System.out.println("Error: Club ID '" + clubId + "' already exists!");
            } else {
                break;
            }
        }

        String clubName = ValidationUtils.readNonEmptyString("Enter Club Name: ");
        String sponsorBrand = ValidationUtils.readNonEmptyString("Enter Sponsor Brand: ");
        double budget = ValidationUtils.readPositiveDouble("Enter Budget: ");

        Club club = new Club(clubId, clubName, sponsorBrand, budget);
        clubs.add(club);
        hasChanges = true;
        System.out.println("Club added successfully!");
    }


    public void searchClubById() {
        System.out.println("\n--- Search Club by ID ---");
        String clubId = ValidationUtils.readNonEmptyString("Enter Club ID to search: ");
        Club club = findClubById(clubId);
        if (club == null) {
            System.out.println("This club does not exist!");
        } else {
            printClubTableHeader();
            club.displayInfo();
            printClubTableFooter();
        }
    }


    public void updateClubById() {
        System.out.println("\n--- Update Club by ID ---");
        String clubId = ValidationUtils.readNonEmptyString("Enter Club ID to update: ");
        Club club = findClubById(clubId);
        if (club == null) {
            System.out.println("This club does not exist!");
            return;
        }

        System.out.println("Current info:");
        printClubTableHeader();
        club.displayInfo();
        printClubTableFooter();

        System.out.println("(Press Enter to skip a field)");

        String newName = ValidationUtils.readOptionalString("New Club Name [" + club.getClubName() + "]: ");
        if (!newName.isEmpty()) {
            club.setClubName(newName);
        }

        String newSponsor = ValidationUtils.readOptionalString("New Sponsor Brand [" + club.getSponsorBrand() + "]: ");
        if (!newSponsor.isEmpty()) {
            club.setSponsorBrand(newSponsor);
        }

        String budgetStr = ValidationUtils.readOptionalString("New Budget [" + club.getBudget() + "]: ");
        if (!budgetStr.isEmpty()) {
            try {
                double newBudget = Double.parseDouble(budgetStr);
                if (newBudget > 0) {
                    club.setBudget(newBudget);
                } else {
                    System.out.println("Error: Budget must be positive! Budget not updated.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number! Budget not updated.");
            }
        }

        hasChanges = true;
        System.out.println("Club updated successfully!");
    }


    public void filterClubsByBudget() {
        System.out.println("\n--- Filter Clubs by Budget ---");
        double maxBudget = ValidationUtils.readPositiveDouble("Enter maximum budget: ");

        List<Club> filtered = new ArrayList<>();
        for (Club club : clubs) {
            if (club.getBudget() <= maxBudget) {
                filtered.add(club);
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("No clubs found with budget <= " + maxBudget);
        } else {
            printClubTableHeader();
            for (Club club : filtered) {
                club.displayInfo();
            }
            printClubTableFooter();
            System.out.println("Found: " + filtered.size() + " club(s).");
        }
    }


    public void saveToFile() {
        FileUtils.writeClubs(filePath, clubs);
        hasChanges = false;
    }


    public void loadFromFile() {
        clubs = FileUtils.readClubs(filePath);
        hasChanges = false;
        System.out.println("Loaded " + clubs.size() + " club(s) from file.");
    }


    public Club findClubById(String clubId) {
        for (Club club : clubs) {
            if (club.getClubId().equalsIgnoreCase(clubId)) {
                return club;
            }
        }
        return null;
    }

 
    private void printClubTableHeader() {
        System.out.println("+------------+---------------------------+----------------------+-----------------+");
        System.out.printf("| %-10s | %-25s | %-20s | %15s |\n", "Club ID", "Club Name", "Sponsor Brand", "Budget");
        System.out.println("+------------+---------------------------+----------------------+-----------------+");
    }


    private void printClubTableFooter() {
        System.out.println("+------------+---------------------------+----------------------+-----------------+");
    }
}
