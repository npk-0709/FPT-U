package factory;

import model.Developer;
import model.Employee;
import model.HR;
import model.Manager;
import model.Tester;

/**
 * Factory ánh xạ tên vai trò sang đúng lớp con của {@link Employee}.
 *
 * <p>Nhờ tập trung việc khởi tạo ở một nơi, phần nghiệp vụ (load/add/update)
 * không phải biết chi tiết có những lớp con nào - khi thêm vai trò mới chỉ
 * cần sửa tại đây (minh hoạ Open/Closed Principle).</p>
 */
public final class EmployeeFactory {

    private EmployeeFactory() {
        // Lớp tiện ích, không cho khởi tạo.
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
