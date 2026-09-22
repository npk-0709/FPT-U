
public class Main {

    public static void main(String[] args) {
        Employee persion1 = new Employee("SE1", "NVA", "ITER", 60000);
        Employee persion2 = new Employee("SE2", "NVB", "CODE", 40000);
        Employee persion3 = new Employee("SE3", "NVC", "HELP", 80000);
        Employee persion4 = new Employee("SE4", "NVD", "NONE", 20000);
        Employee persion5 = new Employee("SE5", "NVE", "XXXX", 30000);

        CusArrList cusE = new CusArrList();

        cusE.addEmployee(persion1);
        cusE.addEmployee(persion2);
        cusE.addEmployee(persion3);
        cusE.addEmployee(persion4);
        cusE.addEmployee(persion5);

        cusE.displayEmployees();

        Employee findE = cusE.getEmployeeById("SE5");

        if (findE != null) {
            System.out.println("Find Employee:");
            System.out.println(findE);
        } else {
            System.out.println("Not Found !");
        }

        System.out.println("List before sort by salary:");
        cusE.displayEmployees();
        System.out.println("List after sort by salary ascending:");
        cusE.sortBySalaryByAsc();
        cusE.displayEmployees();
        System.out.println("List after sort by salary descending:");
        cusE.sortBySalaryByDec();
        cusE.displayEmployees();
        System.out.println("Total Salary: " + cusE.calculateTotalSalary());
        System.out.println("Employees Above Salary:");
        CusArrList aboveSalaryList = cusE.findEmployeesAboveSalary(35000);
        aboveSalaryList.displayEmployees();

        double avgSalary = cusE.getAverageSalary();
        System.out.println("Average Salary: " + String.format("%.2f", avgSalary));

        // ==================== NON-GENERIC VERSION ====================
        System.out.println("\n========== NON-GENERIC VERSION ==========");

        CusArrListNonGeneric cusNG = new CusArrListNonGeneric();

        cusNG.addEmployee(persion1);
        cusNG.addEmployee(persion2);
        cusNG.addEmployee(persion3);
        cusNG.addEmployee(persion4);
        cusNG.addEmployee(persion5);

        System.out.println("Display all (Non-Generic):");
        cusNG.displayEmployees();

        Employee findNG = cusNG.getEmployeeById("SE5");
        if (findNG != null) {
            System.out.println("Find Employee (Non-Generic):");
            System.out.println(findNG);
        } else {
            System.out.println("Not Found !");
        }

        System.out.println("List before sort (Non-Generic):");
        cusNG.displayEmployees();
        System.out.println("List after sort by salary ascending (Non-Generic):");
        cusNG.sortBySalaryByAsc();
        cusNG.displayEmployees();
        System.out.println("List after sort by salary descending (Non-Generic):");
        cusNG.sortBySalaryByDec();
        cusNG.displayEmployees();
        System.out.println("Total Salary (Non-Generic): " + cusNG.calculateTotalSalary());
        System.out.println("Employees Above Salary (Non-Generic):");
        CusArrListNonGeneric aboveSalaryNG = cusNG.findEmployeesAboveSalary(35000);
        aboveSalaryNG.displayEmployees();

        double avgSalaryNG = cusNG.getAverageSalary();
        System.out.println("Average Salary (Non-Generic): " + String.format("%.2f", avgSalaryNG));

    }

}
