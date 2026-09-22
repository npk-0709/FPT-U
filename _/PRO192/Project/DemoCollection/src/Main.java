
import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/**
 *
 * @author Khuong
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Employee persion1 = new Employee("SE1", "NVA", "ITER", 10000);
        Employee persion2 = new Employee("SE2", "NVB", "CODE", 20000);
        Employee persion3 = new Employee("SE3", "NVC", "HELP", 30000);
        Employee persion4 = new Employee("SE4", "NVD", "NONE", 40000);
        Employee persion5 = new Employee("SE5", "NVE", "XXXX", 50000);

        List<Employee> emlist = new ArrayList<>();

        emlist.add(persion1);
        emlist.add(persion2);
        emlist.add(persion3);
        emlist.add(persion4);
        emlist.add(persion5);
        // print employee details using for

        System.out.println("List of Employees:");
        int sizeofList = emlist.size();
        for (int i = 0; i < sizeofList; ++i) {
            Employee currentEmployee = emlist.get(i);
            System.out.println(currentEmployee.toString());
        }
        emlist.add(persion5);
        System.out.println("List of Employees after ADD:");
        sizeofList = emlist.size();
        for (int i = 0; i < sizeofList; ++i) {
            Employee currentEmployee = emlist.get(i);
            System.out.println(currentEmployee.toString());
        }
    }

}
