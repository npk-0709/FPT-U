package model;

public class Developer extends Employee {

    public Developer(String id, String name, double baseSalary,
                     int workingDays, double bonus, String status) {
        super(id, name, baseSalary, workingDays, bonus, status);
    }

    @Override
    public String getRole() {
        return "Developer";
    }

    @Override
    public double roleAllowance() {
        return 0.10 * getBaseSalary();
    }
}
