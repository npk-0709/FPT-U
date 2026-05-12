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
