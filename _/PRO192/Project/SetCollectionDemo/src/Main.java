import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        Student st1 = new Student("001", "KhuongSoSad", 3.5);
        Student st2 = new Student("002", "KimTuyen", 3.8);
        Student st3 = new Student("003", "Toro", 3.9);
        Student st4 = new Student("004", "Bob", 3.6);
        Student st5 = new Student("005", "Alice", 4.0);


        Set<Student> studentSet = new HashSet<>();
        studentSet.add(st1);
        studentSet.add(st2);
        studentSet.add(st3);
        studentSet.add(st4);
        studentSet.add(st5);


        //using tree set
        Set<Student> studentTreeSet = new TreeSet<>();
        studentTreeSet.add(st1);
        studentTreeSet.add(st2);
        studentTreeSet.add(st3);
        studentTreeSet.add(st4);
        studentTreeSet.add(st5);

        for (Student student : studentTreeSet) {
            System.out.println("ID: " + student.getId() + ", Name: " + student.getName() + ", GPA: " + student.getGpa());
        }
    }
}