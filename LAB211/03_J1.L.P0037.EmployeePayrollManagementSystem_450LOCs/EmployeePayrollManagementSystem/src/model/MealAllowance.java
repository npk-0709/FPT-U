package model;

import java.util.Objects;

public class MealAllowance {

    public static final double UNIT_PRICE = 30000.0;

    public static final int MAX_DAYS = 26;

    private String id;
    private String employeeId;
    private String month;
    private int days;

    public MealAllowance(String id, String employeeId, String month, int days) {
        this.id = id;
        this.employeeId = employeeId;
        this.month = month;
        this.days = days;
    }

    public double getAmount() {
        return days * UNIT_PRICE;
    }

    public String getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getMonth() {
        return month;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        if (days >= 0 && days <= MAX_DAYS) {
            this.days = days;
        }
    }

    public boolean sameKey(String employeeId, String month) {
        return this.employeeId.equalsIgnoreCase(employeeId)
                && this.month.equalsIgnoreCase(month);
    }

    public String toDataLine() {
        return String.format("%s, %s, %s, %d", id, employeeId, month, days);
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-6s | %-8s | %3d | %,12.1f",
                id, employeeId, month, days, getAmount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof MealAllowance)) {
            return false;
        }
        MealAllowance other = (MealAllowance) o;
        return employeeId != null && month != null
                && employeeId.equalsIgnoreCase(other.employeeId)
                && month.equalsIgnoreCase(other.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                employeeId == null ? null : employeeId.toLowerCase(),
                month == null ? null : month.toLowerCase());
    }
}
