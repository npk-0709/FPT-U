# HRMS Project - Member 1 Deliverables

## 1. Project Overview

**Role**: Member 1 (Core System & Utilities)
**Goal**: Establish the foundational architecture for the Human Resource Management System, including Entity classes, Input Validation, and File Persistence.

---

## 2. Key Design Concepts

### Q: Why is `Employee` an Abstract Class?

**Answer**:

1.  **Abstraction (Tính Trừu Tượng)**:
    - In the real world, an "Employee" is a generic concept. A specific employee must be either **Full-time** or **Part-time**.
    - Marking the class as `abstract` prevents the accidental creation of a generic `Employee` object (e.g., `new Employee()` is illegal). You must instantiate a concrete type like `new FullTimeEmployee()`.

2.  **Polymorphism & Contract (Đa Hình & Tính Ràng Buộc)**:
    - By defining `abstract double calculateSalary(...)`, the parent class **forces** all child classes to implement their own salary logic.
    - `FullTimeEmployee` must implement it (Basic + OT\*80k).
    - `PartTimeEmployee` must implement it (Basic + OT\*50k).
    - This ensures consistency while allowing specific behaviors for different types.

---

## 3. How to Run

### Requirements

- Java Development Kit (JDK) 8 or higher.
- Console/Terminal supports UTF-8 (optional but recommended for Vietnamese comments).

### Commands (PowerShell)

**1. Compile**

```powershell
& "path\to\javac.exe" -encoding UTF-8 -d bin src/com/hrms/util/*.java src/com/hrms/entity/*.java src/com/hrms/test/*.java
```

**2. Run Verification Test**

```powershell
& "path\to\java.exe" -cp bin com.hrms.test.Member1Test
```

---

## 4. Source Code

### 4.1. Utilities

#### `src/com/hrms/util/Validation.java`

Handles input validation (Date format, positive numbers).

```java
package com.hrms.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validation {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidDate(String dateStr) {
        if (!isNotEmpty(dateStr)) return false;
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_FORMATTER);
    }

    public static boolean isPositiveDouble(String str) {
        if (!isNotEmpty(str)) return false;
        try {
            double value = Double.parseDouble(str);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
```

#### `src/com/hrms/util/FileService.java`

Handles reading and writing to `.txt` files.

```java
package com.hrms.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    public static void saveToFile(String fileName, List<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Data saved to " + fileName + " successfully.");
        } catch (IOException e) {
            System.err.println("Error saving file " + fileName + ": " + e.getMessage());
        }
    }

    public static List<String> loadFromFile(String fileName) {
        List<String> data = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            return data;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    data.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + fileName + ": " + e.getMessage());
        }
        return data;
    }
}
```

### 4.2. Entities

#### `src/com/hrms/entity/Employee.java`

```java
package com.hrms.entity;

import com.hrms.util.Validation;
import java.time.LocalDate;

public abstract class Employee {
    private String id;
    private String name;
    private String department;
    private String jobTitle;
    private LocalDate dateOfJoining;
    private double basicSalary;

    public Employee(String id, String name, String department, String jobTitle, LocalDate dateOfJoining, double basicSalary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.jobTitle = jobTitle;
        this.dateOfJoining = dateOfJoining;
        this.basicSalary = basicSalary;
    }

    public abstract double calculateSalary(double overtimeHours, int absentDays);

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public LocalDate getDateOfJoining() { return dateOfJoining; }
    public void setDateOfJoining(LocalDate dateOfJoining) { this.dateOfJoining = dateOfJoining; }
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Dept: %s | Job: %s | Joined: %s | Basic Salary: %.0f",
                id, name, department, jobTitle, Validation.formatDate(dateOfJoining), basicSalary);
    }

    public String toFileString() {
        return String.format("%s,%s,%s,%s,%s,%.0f",
                id, name, department, jobTitle, Validation.formatDate(dateOfJoining), basicSalary);
    }
}
```

#### `src/com/hrms/entity/FullTimeEmployee.java`

```java
package com.hrms.entity;

import java.time.LocalDate;

public class FullTimeEmployee extends Employee {
    private static final double OVERTIME_RATE = 80000;
    private static final double ABSENCE_PENALTY = 100000;

    public FullTimeEmployee(String id, String name, String department, String jobTitle, LocalDate dateOfJoining, double basicSalary) {
        super(id, name, department, jobTitle, dateOfJoining, basicSalary);
    }

    @Override
    public double calculateSalary(double overtimeHours, int absentDays) {
        return getBasicSalary() + (overtimeHours * OVERTIME_RATE) - (absentDays * ABSENCE_PENALTY);
    }

    @Override
    public String toString() {
        return super.toString() + " | Type: Full-Time";
    }

    @Override
    public String toFileString() {
        return "FULLTIME," + super.toFileString();
    }
}
```

#### `src/com/hrms/entity/PartTimeEmployee.java`

```java
package com.hrms.entity;

import java.time.LocalDate;

public class PartTimeEmployee extends Employee {
    private static final double OVERTIME_RATE = 50000;
    private static final double ABSENCE_PENALTY = 100000;

    public PartTimeEmployee(String id, String name, String department, String jobTitle, LocalDate dateOfJoining, double basicSalary) {
        super(id, name, department, jobTitle, dateOfJoining, basicSalary);
    }

    @Override
    public double calculateSalary(double overtimeHours, int absentDays) {
        return getBasicSalary() + (overtimeHours * OVERTIME_RATE) - (absentDays * ABSENCE_PENALTY);
    }

    @Override
    public String toString() {
        return super.toString() + " | Type: Part-Time";
    }

    @Override
    public String toFileString() {
        return "PARTTIME," + super.toFileString();
    }
}
```

#### `src/com/hrms/entity/Attendance.java`

```java
package com.hrms.entity;

import com.hrms.util.Validation;
import java.time.LocalDate;

public class Attendance {
    private String employeeId;
    private LocalDate date;
    private String status;
    private double overtimeHours;

    public Attendance(String employeeId, LocalDate date, String status, double overtimeHours) {
        this.employeeId = employeeId;
        this.date = date;
        this.status = status;
        this.overtimeHours = overtimeHours;
    }

    public String getEmployeeId() { return employeeId; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(double overtimeHours) { this.overtimeHours = overtimeHours; }

    @Override
    public String toString() {
        return String.format("Date: %s | Status: %s | Overtime: %.1f hours",
                Validation.formatDate(date), status, overtimeHours);
    }

    public String toFileString() {
        return String.format("%s,%s,%s,%.1f",
                employeeId, Validation.formatDate(date), status, overtimeHours);
    }
}
```
