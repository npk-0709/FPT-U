package model;

public class Manager extends Employee {

    public Manager(String id, String name, double baseSalary,
                   int workingDays, double bonus, String status) {
        super(id, name, baseSalary, workingDays, bonus, status);
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    @Override
    public double roleAllowance() {
        return 0.20 * getBaseSalary() + 200;
    }
}
