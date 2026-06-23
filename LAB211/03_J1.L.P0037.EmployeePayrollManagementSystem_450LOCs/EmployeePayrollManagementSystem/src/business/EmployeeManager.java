package business;

import factory.EmployeeFactory;
import model.Employee;
import tools.Inputter;
import tools.Validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp nghiệp vụ trung tâm: quản lý danh sách nhân viên và toàn bộ 9 chức năng.
 * Đây là nơi đặt thuật toán cho từng chức năng; phần nhập liệu và kiểm tra
 * hợp lệ được uỷ thác cho {@code tools}, việc khởi tạo cho {@code factory}.
 */
public class EmployeeManager {

    private final List<Employee> list = new ArrayList<>();
    private final String filePath;

    /** Cờ "có thay đổi chưa lưu" để nhắc lưu trước khi thoát. */
    private boolean dirty = false;

    public EmployeeManager(String filePath) {
        this.filePath = filePath;
    }

    // ----------------------------------------------------------------
    // Function 1: Load from file
    // ----------------------------------------------------------------
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
            System.out.println("Cannot read file '" + filePath + "': " + e.getMessage());
            return;
        }
        dirty = false;
        System.out.println("Loaded " + ok + " record(s), skipped " + bad + " invalid line(s).");
    }

    /**
     * Phân tích một dòng và thêm vào danh sách nếu hợp lệ.
     * Bọc try-catch để không bao giờ crash khi gặp dòng dữ liệu lỗi.
     *
     * @return true nếu thêm thành công, false nếu dòng bị bỏ qua.
     */
    private boolean parseAndAdd(String line) {
        String[] p = line.split("\\s*,\\s*");
        if (p.length != 7) {
            return false;
        }
        try {
            String id = p[0];
            String name = p[1];
            String role = p[2];
            double baseSalary = Double.parseDouble(p[3]);
            int workingDays = Integer.parseInt(p[4]);
            double bonus = Double.parseDouble(p[5]);
            String status = p[6];

            boolean valid = Validator.isEmployeeId(id)
                    && Validator.isNotEmpty(name)
                    && Validator.isRole(role)
                    && Validator.isPositive(baseSalary)
                    && Validator.isWorkingDays(workingDays)
                    && Validator.isNonNegative(bonus)
                    && Validator.isStatus(status)
                    && !exists(id);
            if (!valid) {
                return false;
            }
            list.add(EmployeeFactory.create(id, name, role, baseSalary, workingDays, bonus, status));
            return true;
        } catch (IllegalArgumentException ex) {
            // Bao gồm NumberFormatException (parse số sai) và role không hợp lệ.
            return false; // -> bỏ qua dòng lỗi này
        }
    }

    // ----------------------------------------------------------------
    // Function 2: Add a new employee
    // ----------------------------------------------------------------
    public void add() {
        String id = Inputter.inputByRegex("ID (Exxx): ",
                Validator.ID_REGEX, "Format must be E followed by 3 digits (e.g., E001).");
        if (exists(id)) {
            System.out.println("This employee ID already exists!");
            return;
        }
        String name = Inputter.inputNonEmpty("Name: ");
        String role = Inputter.inputInSet("Role (Developer/Tester/Manager/HR): ", Validator.ROLES);
        double baseSalary = Inputter.inputPositiveDouble("Base salary (> 0): ");
        int workingDays = Inputter.inputInt("Working days (0-26): ", 0, 26);
        double bonus = Inputter.inputNonNegativeDouble("Bonus (>= 0): ");
        String status = Inputter.inputInSet("Status (active/inactive): ", Validator.STATUSES);

        list.add(EmployeeFactory.create(id, name, role, baseSalary, workingDays, bonus, status));
        dirty = true;
        System.out.println("Employee added successfully.");
    }

    // ----------------------------------------------------------------
    // Function 3: Update employee (role, salary, bonus, status)
    // ----------------------------------------------------------------
    public void update() {
        if (isEmpty()) {
            return;
        }
        String id = Inputter.inputByRegex("ID to update: ",
                Validator.ID_REGEX, "Format must be E followed by 3 digits (e.g., E001).");
        Employee e = findById(id);
        if (e == null) {
            System.out.println("This employee does not exist!");
            return;
        }
        System.out.println("Press Enter to keep the current value.");

        String roleStr = Inputter.inputOptional("Role [" + e.getRole() + "]: ");
        String baseStr = Inputter.inputOptional("Base salary [" + (long) e.getBaseSalary() + "]: ");
        String daysStr = Inputter.inputOptional("Working days [" + e.getWorkingDays() + "]: ");
        String bonusStr = Inputter.inputOptional("Bonus [" + (long) e.getBonus() + "]: ");
        String statusStr = Inputter.inputOptional("Status [" + e.getStatus() + "]: ");

        // Gom giá trị mới (giữ giá trị cũ nếu để trống) rồi áp dụng có kiểm tra.
        String newRole = Validator.isRole(roleStr) ? roleStr : e.getRole();

        double newBase = e.getBaseSalary();
        if (!baseStr.isEmpty()) {
            try {
                double v = Double.parseDouble(baseStr);
                if (Validator.isPositive(v)) {
                    newBase = v;
                } else {
                    System.out.println("  -> Base salary must be positive. Keeping old value.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("  -> Invalid base salary. Keeping old value.");
            }
        }

        int newDays = e.getWorkingDays();
        if (!daysStr.isEmpty()) {
            try {
                int v = Integer.parseInt(daysStr);
                if (Validator.isWorkingDays(v)) {
                    newDays = v;
                } else {
                    System.out.println("  -> Working days must be in [0, 26]. Keeping old value.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("  -> Invalid working days. Keeping old value.");
            }
        }

        double newBonus = e.getBonus();
        if (!bonusStr.isEmpty()) {
            try {
                double v = Double.parseDouble(bonusStr);
                if (Validator.isNonNegative(v)) {
                    newBonus = v;
                } else {
                    System.out.println("  -> Bonus must be >= 0. Keeping old value.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("  -> Invalid bonus. Keeping old value.");
            }
        }

        String newStatus = Validator.isStatus(statusStr) ? statusStr : e.getStatus();

        // Đổi role nghĩa là đổi lớp con -> tạo lại đối tượng bằng factory.
        Employee updated = EmployeeFactory.create(
                e.getId(), e.getName(), newRole, newBase, newDays, newBonus, newStatus);
        list.set(list.indexOf(e), updated);
        dirty = true;
        System.out.println("Updated successfully.");
    }

    // ----------------------------------------------------------------
    // Function 4: Remove an employee by ID
    // ----------------------------------------------------------------
    public void remove() {
        if (isEmpty()) {
            return;
        }
        String id = Inputter.inputByRegex("ID to remove: ",
                Validator.ID_REGEX, "Format must be E followed by 3 digits (e.g., E001).");
        Employee e = findById(id);
        if (e == null) {
            System.out.println("This employee does not exist!");
            return;
        }
        if (Inputter.confirmYesNo("Are you sure you want to remove " + e.getName() + "? (Y/N): ")) {
            list.remove(e);
            dirty = true;
            System.out.println("Removed successfully.");
        } else {
            System.out.println("Cancelled. No employee was removed.");
        }
    }

    // ----------------------------------------------------------------
    // Function 5: Search employees by attribute
    // ----------------------------------------------------------------
    public void searchByAttribute() {
        if (isEmpty()) {
            return;
        }
        System.out.println("Search by: 1. ID  2. Name  3. Role  4. Status");
        int key = Inputter.inputInt("Choose 1-4: ", 1, 4);
        String keyword = Inputter.inputNonEmpty("Keyword: ").toLowerCase();

        List<Employee> found = new ArrayList<>();
        for (Employee e : list) {
            String field;
            switch (key) {
                case 1:
                    field = e.getId();
                    break;
                case 2:
                    field = e.getName();
                    break;
                case 3:
                    field = e.getRole();
                    break;
                default:
                    field = e.getStatus();
                    break;
            }
            if (field.toLowerCase().contains(keyword)) {
                found.add(e);
            }
        }

        if (found.isEmpty()) {
            System.out.println("No employee matches your keyword.");
        } else {
            printTable(found);
        }
    }

    // ----------------------------------------------------------------
    // Function 6: Calculate monthly payroll (active employees only)
    // ----------------------------------------------------------------
    public void monthlyPayroll() {
        if (isEmpty()) {
            return;
        }
        double total = 0;
        boolean any = false;
        System.out.printf("%-5s | %-15s | %-10s | %12s%n", "ID", "Name", "Role", "Salary");
        printLine();
        for (Employee e : list) {
            if (e.isActive()) {
                double salary = e.calculateSalary(); // đa hình theo vai trò
                System.out.printf("%-5s | %-15s | %-10s | %,12.1f%n",
                        e.getId(), e.getName(), e.getRole(), salary);
                total += salary;
                any = true;
            }
        }
        printLine();
        if (!any) {
            System.out.println("No active employee to calculate payroll.");
        } else {
            System.out.printf("TOTAL monthly payroll (active only): %,.1f%n", total);
        }
    }

    // ----------------------------------------------------------------
    // Function 7: Display employee list
    // ----------------------------------------------------------------
    public void display() {
        if (isEmpty()) {
            return;
        }
        printTable(list);
    }

    // ----------------------------------------------------------------
    // Function 8: Save data to file
    // ----------------------------------------------------------------
    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Employee e : list) {
                pw.println(e.toDataLine());
            }
            dirty = false;
            System.out.println("Data saved to '" + filePath + "' (" + list.size() + " record(s)).");
        } catch (IOException ex) {
            System.out.println("Save failed: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Function 9: Quit program (confirm saving before exit)
    // ----------------------------------------------------------------
    public void quit() {
        if (dirty) {
            if (Inputter.confirmYesNo("You have unsaved changes. Save before exit? (Y/N): ")) {
                save();
            }
        }
        System.out.println("Goodbye!");
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    public boolean exists(String id) {
        return findById(id) != null;
    }

    private Employee findById(String id) {
        for (Employee e : list) {
            if (e.getId().equalsIgnoreCase(id)) {
                return e;
            }
        }
        return null;
    }

    private boolean isEmpty() {
        if (list.isEmpty()) {
            System.out.println("The employee list is empty.");
            return true;
        }
        return false;
    }

    private void printTable(List<Employee> employees) {
        System.out.printf("%-5s | %-15s | %-10s | %8s | %3s | %7s | %-8s | %10s%n",
                "ID", "Name", "Role", "Base", "Day", "Bonus", "Status", "Salary");
        printLine();
        for (Employee e : employees) {
            System.out.println(e);
        }
        printLine();
        System.out.println("Total: " + employees.size() + " employee(s).");
    }

    private void printLine() {
        System.out.println("---------------------------------------------------------------------------------");
    }
}
