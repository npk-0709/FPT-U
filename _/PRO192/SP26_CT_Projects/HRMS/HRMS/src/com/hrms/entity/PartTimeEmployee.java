package com.hrms.entity;

import java.time.LocalDate;

public class PartTimeEmployee extends Employee {
    private static final double OVERTIME_RATE = 50000;
    private static final double ABSENCE_PENALTY = 100000;

    public PartTimeEmployee(String id, String name, String department, String jobTitle, LocalDate dateOfJoining,
            double basicSalary) {
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
