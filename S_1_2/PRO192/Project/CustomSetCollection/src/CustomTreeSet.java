import java.util.TreeSet;

public class CustomTreeSet extends TreeSet<Student> {
    public CustomTreeSet() {
        super();
    }

    public void addStudent(Student student) {
        this.add(student);
    }

    public void displayStudents() {
        for (Student student : this) {
            System.out.println("ID: " + student.getId() + ", Name: " + student.getName() + ", GPA: " + student.getGpa());
        }
    }

    public Student getFrist() {
        if (this.isEmpty()) {
            return null;
        }
        return this.first();
    }

    public Student getLast() {
        if (this.isEmpty()) {
            return null;
        }
        return this.last();
    }

    public double getAverageGpa() {
        if (this.isEmpty()) {
            return 0.0;
        }
        double totalGpa = 0.0;
        for (Student student : this) {
            totalGpa += student.getGpa();
        }
        return totalGpa / this.size();
    }
    public  void removeStudent(Student student) {
        this.remove(student);
    }
}
