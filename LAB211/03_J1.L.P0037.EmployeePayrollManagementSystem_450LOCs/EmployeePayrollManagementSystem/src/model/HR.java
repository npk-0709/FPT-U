package model;

/** Nhân sự: phụ cấp 8% lương cơ bản. */
public class HR extends Employee {

    public HR(String id, String name, double baseSalary,
              int workingDays, double bonus, String status) {
        super(id, name, baseSalary, workingDays, bonus, status);
    }

    @Override
    public String getRole() {
        return "HR";
    }

    @Override
    public double roleAllowance() {
        return 0.08 * getBaseSalary();
    }
}
