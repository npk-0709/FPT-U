//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Student student1 = new Student("123", "Alice", 3.5);
        Student student2 = new Student("456", "Bob", 3.8);
        Student student3 = new Student("789", "Charlie", 3.5);
        Student student4 = new Student("789", "Bob", 3.8);
        Student student5 = new Student("789", "Charlie", 3.5);
        HashMap<String, Student> stmap = new HashMap<>();
        stmap.put("0363561621", student1);
        stmap.put("0363561622", student2);
        stmap.put("0363561623", student3);
        stmap.put("0363561624", student4);
        stmap.put("0363561625", student5);
        String phoneNumber = "0363561621";
        Student student = stmap.get(phoneNumber);
        if (student != null) {
            System.out.println("Student found: " + student.getName() + ", GPA: " + student.getGpa());
        } else {
            System.out.println("Student not found for phone number: " + phoneNumber);
        }
        for (Map.Entry<String, Student> entry : stmap.entrySet()) {
            String key = entry.getKey();
            Student value = entry.getValue();
            System.out.println("Phone Number: " + key + ", Student Name: " + value.getName() + ", GPA: " + value.getGpa());
        }


    }
}

