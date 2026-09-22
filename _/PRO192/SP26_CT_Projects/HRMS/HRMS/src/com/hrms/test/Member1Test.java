package com.hrms.test;

import com.hrms.entity.*;
import com.hrms.util.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Member1Test {
    public static void main(String[] args) {
        System.out.println("=== Testing Utilities ===");
        System.out.println("isValidDate('20/11/2023'): " + Validation.isValidDate("20/11/2023"));
        System.out.println("isValidDate('invalid'): " + Validation.isValidDate("invalid"));
        System.out.println("isPositiveDouble('5000'): " + Validation.isPositiveDouble("5000"));
        System.out.println("isPositiveDouble('-100'): " + Validation.isPositiveDouble("-100"));

        System.out.println("\n=== Testing Entities ===");
        LocalDate joinDate = LocalDate.of(2023, 1, 15);
        FullTimeEmployee emp1 = new FullTimeEmployee("E001", "Nguyen Van A", "IT", "Developer", joinDate, 10000000);
        PartTimeEmployee emp2 = new PartTimeEmployee("E002", "Tran Thi B", "HR", "Assistant", joinDate, 5000000);

        System.out.println(emp1);
        System.out.println("Salary (10h OT, 0 absent): " + emp1.calculateSalary(10, 0));

        System.out.println(emp2);
        System.out.println("Salary (10h OT, 0 absent): " + emp2.calculateSalary(10, 0));

        System.out.println("\n=== Testing FileService ===");
        List<String> data = new ArrayList<>();
        data.add(emp1.toFileString());
        data.add(emp2.toFileString());

        String filename = "employees_test.txt";
        FileService.saveToFile(filename, data);

        List<String> loadedData = FileService.loadFromFile(filename);
        System.out.println("Loaded " + loadedData.size() + " lines from file:");
        for (String line : loadedData) {
            System.out.println(line);
        }

        // Clean up
        new java.io.File(filename).delete();
    }
}
