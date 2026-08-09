package business;

import model.MealAllowance;
import tools.Inputter;
import tools.Validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MealAllowanceManager {

    private final List<MealAllowance> list = new ArrayList<>();
    private final String filePath;
    private final EmployeeManager employeeManager;

    private boolean dirty = false;

    public MealAllowanceManager(String filePath, EmployeeManager employeeManager) {
        this.filePath = filePath;
        this.employeeManager = employeeManager;
    }

    public void run() {
        load();
        int choice;
        do {
            showMenu();
            choice = Inputter.inputInt("Choose 1-6: ", 1, 6);
            System.out.println();
            switch (choice) {
                case 1:
                    add();
                    break;
                case 2:
                    display();
                    break;
                case 3:
                    update();
                    break;
                case 4:
                    remove();
                    break;
                case 5:
                    save();
                    break;
                case 6:
                    exit();
                    break;
                default:
                    break;
            }
            System.out.println();
        } while (choice != 6);
    }

    private void showMenu() {
        System.out.println("----- MEAL ALLOWANCE (PHU CAP AN) -----");
        System.out.println("Unit price: " + String.format("%,.0f", MealAllowance.UNIT_PRICE) + " / day");
        System.out.println("1. Create meal allowance");
        System.out.println("2. Read (print allowance with amount)");
        System.out.println("3. Update days (recalculate amount)");
        System.out.println("4. Delete by id");
        System.out.println("5. Save to file");
        System.out.println("6. Back to main menu");
        System.out.println("---------------------------------------");
    }

    public void load() {
        int ok = 0;
        int bad = 0;
        list.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (parseAndAdd(line)) {
                    ok++;
                } else {
                    bad++;
                }
            }
        } catch (IOException e) {
            System.out.println("No meal allowance file yet (will be created on save).");
            return;
        }
        dirty = false;
        System.out.println("Loaded " + ok + " meal allowance(s), skipped " + bad + " invalid line(s).");
    }

    private boolean parseAndAdd(String line) {
        String[] p = line.split("\\s*,\\s*");
        if (p.length != 4) {
            return false;
        }
        try {
            String id = p[0];
            String employeeId = p[1];
            String month = p[2];
            int days = Integer.parseInt(p[3]);

            boolean valid = Validator.isMealAllowanceId(id)
                    && Validator.isEmployeeId(employeeId)
                    && Validator.isMonth(month)
                    && Validator.isMealDays(days)
                    && !existsId(id)
                    && !existsKey(employeeId, month);
            if (!valid) {
                return false;
            }
            list.add(new MealAllowance(id, employeeId, month, days));
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public void add() {
        String id = Inputter.inputByRegex("ID (ML-xxxx): ",
                Validator.MEAL_ALLOWANCE_ID_REGEX, "Format must be ML- followed by 4 digits (e.g., ML-0001).");
        if (existsId(id)) {
            System.out.println("This meal allowance ID already exists!");
            return;
        }
        String employeeId = Inputter.inputByRegex("Employee ID (Exxx): ",
                Validator.ID_REGEX, "Format must be E followed by 3 digits (e.g., E001).");
        if (!employeeManager.exists(employeeId)) {
            System.out.println("This employee does not exist!");
            return;
        }
        String month = Inputter.inputByRegex("Month (MM/yyyy): ",
                Validator.MONTH_REGEX, "Format must be MM/yyyy (e.g., 06/2026).");
        if (existsKey(employeeId, month)) {
            System.out.println("A meal allowance for this employee and month already exists!");
            return;
        }
        int days = Inputter.inputInt("Days (0-26): ", 0, 26);

        list.add(new MealAllowance(id, employeeId, month, days));
        dirty = true;
        System.out.println("Meal allowance created. Amount = " + String.format("%,.1f", days * MealAllowance.UNIT_PRICE));
    }

    public void display() {
        if (isEmpty()) {
            return;
        }
        printTable(list);
    }

    public void update() {
        if (isEmpty()) {
            return;
        }
        String id = Inputter.inputByRegex("ID to update: ",
                Validator.MEAL_ALLOWANCE_ID_REGEX, "Format must be ML- followed by 4 digits (e.g., ML-0001).");
        MealAllowance m = findById(id);
        if (m == null) {
            System.out.println("This meal allowance does not exist!");
            return;
        }
        System.out.println("Current days: " + m.getDays()
                + " | amount: " + String.format("%,.1f", m.getAmount()));
        int newDays = Inputter.inputInt("New days (0-26): ", 0, 26);
        m.setDays(newDays);
        dirty = true;
        System.out.println("Updated. New amount = " + String.format("%,.1f", m.getAmount()));
    }

    public void remove() {
        if (isEmpty()) {
            return;
        }
        String id = Inputter.inputByRegex("ID to remove: ",
                Validator.MEAL_ALLOWANCE_ID_REGEX, "Format must be ML- followed by 4 digits (e.g., ML-0001).");
        MealAllowance m = findById(id);
        if (m == null) {
            System.out.println("This meal allowance does not exist!");
            return;
        }
        if (Inputter.confirmYesNo("Are you sure you want to remove " + m.getId() + "? (Y/N): ")) {
            list.remove(m);
            dirty = true;
            System.out.println("Removed successfully.");
        } else {
            System.out.println("Cancelled. No record was removed.");
        }
    }

    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (MealAllowance m : list) {
                pw.println(m.toDataLine());
            }
            dirty = false;
            System.out.println("Data saved to '" + filePath + "' (" + list.size() + " record(s)).");
        } catch (IOException ex) {
            System.out.println("Save failed: " + ex.getMessage());
        }
    }

    private void exit() {
        if (dirty) {
            if (Inputter.confirmYesNo("You have unsaved changes. Save before leaving? (Y/N): ")) {
                save();
            }
        }
    }

    private boolean existsId(String id) {
        return findById(id) != null;
    }

    private boolean existsKey(String employeeId, String month) {
        for (MealAllowance m : list) {
            if (m.sameKey(employeeId, month)) {
                return true;
            }
        }
        return false;
    }

    private MealAllowance findById(String id) {
        for (MealAllowance m : list) {
            if (m.getId().equalsIgnoreCase(id)) {
                return m;
            }
        }
        return null;
    }

    private boolean isEmpty() {
        if (list.isEmpty()) {
            System.out.println("The meal allowance list is empty.");
            return true;
        }
        return false;
    }

    private void printTable(List<MealAllowance> records) {
        System.out.printf("%-8s | %-6s | %-8s | %3s | %12s%n",
                "ID", "EmpID", "Month", "Day", "Amount");
        printLine();
        for (MealAllowance m : records) {
            System.out.println(m);
        }
        printLine();
        System.out.println("Total: " + records.size() + " record(s).");
    }

    private void printLine() {
        System.out.println("------------------------------------------------");
    }
}
