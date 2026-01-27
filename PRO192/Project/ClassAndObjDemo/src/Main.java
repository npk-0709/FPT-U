
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner myScanner = new Scanner(System.in);

        System.out.println("Enter ID: ");
        String id = myScanner.nextLine();
        System.out.println("Enter NAME: ");
        String name = myScanner.nextLine();
        System.out.println("Enter birthYear: ");
        int year = myScanner.nextInt();
        Student studentOne = new Student(id, name, year, "SE", "SE2000");

        studentOne.registerCourse("PRO192");
        studentOne.payFee(5500000);
        studentOne.showInfo();
        studentOne.changeClass("SE9999");
        studentOne.changeMajor("SS");
        studentOne.showInfo();
        
        
        Student studentTwo = new Student("SE999999999");
        
        studentTwo.showInfo();
        
        System.out.println(studentOne.toString());
    }

}
