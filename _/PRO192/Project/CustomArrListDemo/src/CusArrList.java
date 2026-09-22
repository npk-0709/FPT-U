
import java.util.ArrayList;
import java.util.Comparator;

public class CusArrList extends ArrayList<Employee> {

    public void addEmployee(Employee employee) {
        add(employee);
    }
    // display all employees

    public void displayEmployees() {
        int sizeOfList = size();
        for (int i = 0; i < sizeOfList; ++i) {
            Employee currentEmployee = get(i);
            System.out.println(currentEmployee.toString());
        }
    }

    public Employee getEmployeeById(String id) {
        int sizeOfList = size();
        for (int i = 0; i < sizeOfList; ++i) {
            Employee currentEmployee = get(i);
            if (currentEmployee.getId().equalsIgnoreCase(id)) {
                return currentEmployee;
            }
        }
        return null;
    }

    public void sortBySalaryByAsc() {

        Comparator<Employee> comparator = new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {
                if (e1.getBasic_salary() < e2.getBasic_salary()) {
                    return -1;
                } else if (e1.getBasic_salary() > e2.getBasic_salary()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        this.sort(comparator);
    }

    public void sortBySalaryByDec() {

        Comparator<Employee> comparator2 = new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {
                if (e1.getBasic_salary() < e2.getBasic_salary()) {
                    return -1;
                } else if (e1.getBasic_salary() > e2.getBasic_salary()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        this.sort(comparator2.reversed());
    }

    //calculate total salary
    public double calculateTotalSalary() {
        double total = 0;
        int sizeOfList = size();
        for (int i = 0; i < sizeOfList; ++i) {
            Employee currentEmployee = get(i);
            total += currentEmployee.getBasic_salary();
        }
        return total;
    }

    //find employee above a certain salary
    public CusArrList findEmployeesAboveSalary(double salary) {
        CusArrList result = new CusArrList();
        int sizeOfList = size();
        for (int i = 0; i < sizeOfList; ++i) {
            Employee currentEmployee = get(i); //get
            if (currentEmployee.getBasic_salary() > salary) {
                result.addEmployee(currentEmployee);
            }
        }
        return result;
    }
    //get average salary
    public double getAverageSalary() {
        double totalSalary = calculateTotalSalary();
        int sizeOfList = size();
        if (sizeOfList == 0) {
            return 0;
        }
        return totalSalary / sizeOfList;
    }
}
