package main;

import business.EmployeeManager;
import tools.Inputter;

import java.util.Locale;

/**
 * Điểm vào chương trình: hiển thị menu và điều phối tới các chức năng nghiệp vụ.
 * Employee Payroll Management System - J1.L.P0037 (LAB211).
 */
public class Main {

    private static final String DATA_FILE = "employees.txt";

    public static void main(String[] args) {
        // Cố định locale để định dạng số (dấu phân cách hàng nghìn/thập phân) luôn nhất quán.
        Locale.setDefault(Locale.US);
        EmployeeManager manager = new EmployeeManager(DATA_FILE);
        int choice;
        do {
            showMenu();
            choice = Inputter.inputInt("Choose 1-9: ", 1, 9);
            System.out.println();
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
                    manager.quit();
                    break;
                default:
                    break;
            }
            System.out.println();
        } while (choice != 9);
    }

    private static void showMenu() {
        System.out.println("===== EMPLOYEE PAYROLL MANAGEMENT SYSTEM =====");
        System.out.println("1. Load employee data from file");
        System.out.println("2. Add a new employee");
        System.out.println("3. Update employee information");
        System.out.println("4. Remove an employee by ID");
        System.out.println("5. Search employees by attribute");
        System.out.println("6. Calculate monthly payroll");
        System.out.println("7. Display employee list");
        System.out.println("8. Save data to file");
        System.out.println("9. Quit program");
        System.out.println("==============================================");
    }
}
