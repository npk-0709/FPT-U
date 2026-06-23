package model;

import java.util.Objects;

/**
 * Lớp trừu tượng mô tả một nhân viên - phần "cốt lõi" chung cho mọi vai trò.
 *
 * <p>Áp dụng đủ 4 nguyên lý OOP:</p>
 * <ul>
 *   <li><b>Abstraction</b>: {@link #getRole()} và {@link #roleAllowance()} là abstract,
 *       mỗi vai trò tự định nghĩa; lớp này chỉ giữ thuộc tính/hành vi chung.</li>
 *   <li><b>Encapsulation</b>: field để private, truy cập qua getter/setter có kiểm tra.</li>
 *   <li><b>Inheritance</b>: Developer/Tester/Manager/HR kế thừa lớp này.</li>
 *   <li><b>Polymorphism</b>: {@link #calculateSalary()} dùng {@code roleAllowance()}
 *       được override ở lớp con.</li>
 * </ul>
 */
public abstract class Employee implements Payable {

    /** Số ngày công tối đa trong tháng, dùng làm mẫu số quy đổi lương ngày công. */
    public static final int STANDARD_WORKING_DAYS = 26;

    private String id;
    private String name;
    private double baseSalary;
    private int workingDays;
    private double bonus;
    private String status; // "active" hoặc "inactive"

    protected Employee(String id, String name, double baseSalary,
                       int workingDays, double bonus, String status) {
        this.id = id;
        this.name = name;
        this.baseSalary = baseSalary;
        this.workingDays = workingDays;
        this.bonus = bonus;
        this.status = status;
    }

    /** Mỗi lớp con trả về tên vai trò tương ứng. */
    public abstract String getRole();

    /** Phụ cấp theo vai trò - điểm khác biệt được override (đa hình). */
    public abstract double roleAllowance();

    /**
     * Công thức lương tháng (đề không cho sẵn nên tự định nghĩa hợp lý):
     * <pre>salary = baseSalary / 26 * workingDays + bonus + roleAllowance()</pre>
     * Phần roleAllowance() là đa hình nên không cần if-else theo vai trò.
     */
    @Override
    public double calculateSalary() {
        double salaryByDays = baseSalary / STANDARD_WORKING_DAYS * workingDays;
        return salaryByDays + bonus + roleAllowance();
    }

    /** Nhân viên đang làm việc (không phân biệt hoa thường). */
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

    /** Định dạng đúng cấu trúc tệp: id, name, role, baseSalary, workingDays, bonus, status */
    public String toDataLine() {
        return String.format("%s, %s, %s, %.0f, %d, %.0f, %s",
                id, name, getRole(), baseSalary, workingDays, bonus, status);
    }

    @Override
    public String toString() {
        return String.format("%-5s | %-15s | %-10s | %8.0f | %3d | %7.0f | %-8s | %,10.1f",
                id, name, getRole(), baseSalary, workingDays, bonus, status, calculateSalary());
    }

    /** Hai nhân viên bằng nhau khi trùng ID (ID là khoá chính). */
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
