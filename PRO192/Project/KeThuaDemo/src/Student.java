
public class Student extends Person {

    private double gpa;

    public Student(String name, int age, String address, String phone, double gpa) {
        super(name, age, address, phone);
        this.gpa = gpa;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    @Override
    public String toString() {
        return "GPA=" + gpa + "-" + super.toString();
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Info ===");
        System.out.println("Name: " + getName());
        System.out.println("Age: " + getAge());
        System.out.println("Adrr: " + getAddress());
        System.out.println("Phone: " + getPhone());
        System.out.println("Gpa: " + getGpa());

    }

}
