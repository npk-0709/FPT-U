
public class CusArrListNonGeneric {

    private Object[] data;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public CusArrListNonGeneric() {
        data = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    // Tăng dung lượng mảng khi đầy
    private void grow() {
        int newCapacity = data.length * 2;
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    // Thêm Employee
    public void addEmployee(Employee employee) {
        if (size == data.length) {
            grow();
        }
        data[size++] = employee;
    }

    // Lấy phần tử theo index (cần ép kiểu)
    public Employee get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (Employee) data[index]; // ép kiểu từ Object -> Employee
    }

    // Lấy số lượng phần tử
    public int size() {
        return size;
    }

    // Hiển thị tất cả Employee
    public void displayEmployees() {
        for (int i = 0; i < size; i++) {
            Employee currentEmployee = (Employee) data[i]; // ép kiểu
            System.out.println(currentEmployee.toString());
        }
    }

    // Tìm Employee theo ID
    public Employee getEmployeeById(String id) {
        for (int i = 0; i < size; i++) {
            Employee currentEmployee = (Employee) data[i]; // ép kiểu
            if (currentEmployee.getId().equalsIgnoreCase(id)) {
                return currentEmployee;
            }
        }
        return null;
    }

    // Sắp xếp tăng dần theo lương (Bubble Sort)
    public void sortBySalaryByAsc() {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                Employee e1 = (Employee) data[j];
                Employee e2 = (Employee) data[j + 1];
                if (e1.getBasic_salary() > e2.getBasic_salary()) {
                    // swap
                    Object temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;
                }
            }
        }
    }

    // Sắp xếp giảm dần theo lương (Bubble Sort)
    public void sortBySalaryByDec() {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                Employee e1 = (Employee) data[j];
                Employee e2 = (Employee) data[j + 1];
                if (e1.getBasic_salary() < e2.getBasic_salary()) {
                    // swap
                    Object temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;
                }
            }
        }
    }

    // Tính tổng lương
    public double calculateTotalSalary() {
        double total = 0;
        for (int i = 0; i < size; i++) {
            Employee currentEmployee = (Employee) data[i]; // ép kiểu
            total += currentEmployee.getBasic_salary();
        }
        return total;
    }

    // Tìm Employee có lương trên một mức nhất định
    public CusArrListNonGeneric findEmployeesAboveSalary(double salary) {
        CusArrListNonGeneric result = new CusArrListNonGeneric();
        for (int i = 0; i < size; i++) {
            Employee currentEmployee = (Employee) data[i]; // ép kiểu
            if (currentEmployee.getBasic_salary() > salary) {
                result.addEmployee(currentEmployee);
            }
        }
        return result;
    }

    // Tính lương trung bình
    public double getAverageSalary() {
        if (size == 0) {
            return 0;
        }
        return calculateTotalSalary() / size;
    }
}
