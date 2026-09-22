package model;

import java.util.Objects;

public abstract class Employee implements Payable {

    public static final int STANDARD_WORKING_DAYS = 26;

    private String id;
    private String name;
    private double baseSalary;
    private int workingDays;
    private double bonus;
    private String status;

    protected Employee(String id, String name, double baseSalary,
                       int workingDays, double bonus, String status) {
        this.id = id;
        this.name = name;
        this.baseSalary = baseSalary;
        this.workingDays = workingDays;
        this.bonus = bonus;
        this.status = status;
    }

    public abstract String getRole();

    public abstract double roleAllowance();

    @Override
    public double calculateSalary() {
        double salaryByDays = baseSalary / STANDARD_WORKING_DAYS * workingDays;
        return salaryByDays + bonus + roleAllowance();
    }

    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        if (baseSalary > 0) {
            this.baseSalary = baseSalary;
        }
    }

    public int getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(int workingDays) {
        if (workingDays >= 0 && workingDays <= STANDARD_WORKING_DAYS) {
            this.workingDays = workingDays;
        }
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        if (bonus >= 0) {
            this.bonus = bonus;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if ("active".equalsIgnoreCase(status) || "inactive".equalsIgnoreCase(status)) {
            this.status = status.toLowerCase();
        }
    }

    public String toDataLine() {
        return String.format("%s, %s, %s, %.0f, %d, %.0f, %s",
                id, name, getRole(), baseSalary, workingDays, bonus, status);
    }

    @Override
    public String toString() {
        return String.format("%-5s | %-15s | %-10s | %8.0f | %3d | %7.0f | %-8s | %,10.1f",
                id, name, getRole(), baseSalary, workingDays, bonus, status, calculateSalary());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) o;
        return id != null && id.equalsIgnoreCase(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id == null ? null : id.toLowerCase());
    }
}
