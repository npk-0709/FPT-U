package factory;

import model.Developer;
import model.Employee;
import model.HR;
import model.Manager;
import model.Tester;

public final class EmployeeFactory {

    private EmployeeFactory() {
    }

    public static Employee create(String id, String name, String role,
                                  double baseSalary, int workingDays,
                                  double bonus, String status) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        switch (role.trim().toLowerCase()) {
            case "developer":
                return new Developer(id, name, baseSalary, workingDays, bonus, status);
            case "tester":
                return new Tester(id, name, baseSalary, workingDays, bonus, status);
            case "manager":
                return new Manager(id, name, baseSalary, workingDays, bonus, status);
            case "hr":
                return new HR(id, name, baseSalary, workingDays, bonus, status);
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
