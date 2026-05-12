public class Main {
    public static void main(String[] args) {
        Student student1 = new Student("001", "Alice", 3.5);
        Student student2 = new Student("002", "Bob", 3.8);
        Student student3 = new Student("003", "Charlie", 3.2);
        Student student4 = new Student("004", "David", 3.8);
        Student student5 = new Student("005", "Alex", 3.9);

        CustomTreeSet studentSet = new CustomTreeSet();
        studentSet.addStudent(student1);
        studentSet.addStudent(student2);
        studentSet.addStudent(student3);
        studentSet.addStudent(student4);
        studentSet.addStudent(student5);

        System.out.println("Students in the CustomTreeSet:");
        studentSet.displayStudents();
        Student firstStudent = studentSet.getFrist();
        if (firstStudent != null) {
            System.out.println("\nFirst Student: ID: " + firstStudent.getId() + ", Name: " + firstStudent.getName() + ", GPA: " + firstStudent.getGpa());
        } else {
            System.out.println("\nThe CustomTreeSet is empty.");
        }
        Student lastStudent = studentSet.getLast();
        if (lastStudent != null) {
            System.out.println("\nLast Student: ID: " + lastStudent.getId() + ", Name: " + lastStudent.getName() + ", GPA: " + lastStudent.getGpa());
        } else {
            System.out.println("\nThe CustomTreeSet is empty.");
        }
        double averageGpa = studentSet.getAverageGpa();
        System.out.println("\nAverage GPA: " + averageGpa);

        studentSet.removeStudent(student2);
        System.out.println("\nStudents after removing Bob:");
    }
}