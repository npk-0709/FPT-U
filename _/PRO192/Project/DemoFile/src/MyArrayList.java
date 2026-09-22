import java.util.ArrayList;
import java.util.List;

public class MyArrayList {
    private List<Employee> employeeList;

    public MyArrayList() {
        this.employeeList = new ArrayList<>();
    }

    public void addEmployee(Employee employee) {
        employeeList.add(employee);
    }

    public void displayAllEmployees() {
        if (employeeList.isEmpty()) {
            System.out.println("No employees in the list.");
            return;
        }
        for (Employee employee : employeeList) {
            System.out.println(employee.toString());
        }
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }
}
