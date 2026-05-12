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

    public String getEmployeeId() {
        return employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

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
