package main;

import business.AuthService;
import business.EmployeeManager;
import business.MealAllowanceManager;
import model.Account;
import tools.Inputter;

import java.util.Locale;

public class Main {

    private static final String DATA_FILE = "employees.txt";
    private static final String MEAL_DATA_FILE = "meal_allowances.txt";

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        AuthService authService = new AuthService();
        Account currentUser = authService.login();
        if (currentUser == null) {
            System.out.println("Too many failed attempts. Program terminated.");
            return;
        }

        EmployeeManager manager = new EmployeeManager(DATA_FILE);
        MealAllowanceManager mealManager = new MealAllowanceManager(MEAL_DATA_FILE, manager);
        int choice;
        do {
            showMenu(currentUser);
            choice = Inputter.inputInt("Choose 1-10: ", 1, 10);
            System.out.println();

            if (!currentUser.canAccess(choice)) {
                System.out.println("Access denied. Your role (" + currentUser.getRole()
                        + ") is not allowed to use this function.");
                System.out.println();
                continue;
            }

            switch (choice) {
                case 1:
                    manager.load();
                    break;
                case 2:
                    manager.add();
                    break;
                case 3:
                    manager.update();
                    break;
                case 4:
                    manager.remove();
                    break;
                case 5:
                    manager.searchByAttribute();
                    break;
                case 6:
                    manager.monthlyPayroll();
                    break;
                case 7:
                    manager.display();
                    break;
                case 8:
                    manager.save();
                    break;
                case 9:
                    mealManager.run();
                    break;
                case 10:
                    manager.quit();
                    break;
                default:
                    break;
            }
            System.out.println();
        } while (choice != 10);
    }

    private static void showMenu(Account user) {
        String[] features = {
                "Load employee data from file",
                "Add a new employee",
                "Update employee information",
                "Remove an employee by ID",
                "Search employees by attribute",
                "Calculate monthly payroll",
                "Display employee list",
                "Save data to file",
                "Meal allowance management (Phu cap an)",
                "Quit program"
        };
        System.out.println("===== EMPLOYEE PAYROLL MANAGEMENT SYSTEM =====");
        System.out.println("Logged in as: " + user);
        System.out.println("----------------------------------------------");
        for (int i = 0; i < features.length; i++) {
            int feature = i + 1;
            String lock = user.canAccess(feature) ? "" : "   [locked]";
            System.out.println(feature + ". " + features[i] + lock);
        }
        System.out.println("==============================================");
    }
}
