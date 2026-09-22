package com.hrms.entity;

import java.time.LocalDate;

public class FullTimeEmployee extends Employee {
    private static final double OVERTIME_RATE = 80000;
    private static final double ABSENCE_PENALTY = 100000;

    public FullTimeEmployee(String id, String name, String department, String jobTitle, LocalDate dateOfJoining,
            double basicSalary) {
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
