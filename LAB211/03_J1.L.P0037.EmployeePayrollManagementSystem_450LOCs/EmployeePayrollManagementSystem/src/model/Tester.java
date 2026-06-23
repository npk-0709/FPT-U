package model;

public class Tester extends Employee {

    public Tester(String id, String name, double baseSalary,
                  int workingDays, double bonus, String status) {
        super(id, name, baseSalary, workingDays, bonus, status);
    }

    @Override
    public String getRole() {
        return "Tester";
    }

    @Override
    public double roleAllowance() {
        return 0.05 * getBaseSalary();
    }
}
