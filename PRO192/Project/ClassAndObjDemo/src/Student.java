/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Khuong
 */
public class Student {

    private String id;
    private String name;
    private int birthYear;
    private String major;
    private String className;

    public Student(String id) {
        this.id = id;
    }

    public Student() {

    } // 

    public Student(String id, String name, int birthYear, String major, String className) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
        this.major = major;
        this.className = className;
    }

    public void changeClass(String newClass) {
        String oldClass = className;
        className = newClass;
        System.out.println(name + " has changed class From : " + oldClass + " To: " + className);
    }

    public void changeMajor(String newMajor) {
        newMajor = newMajor.trim();
        if (!newMajor.equals("SE") || !newMajor.equals("SS")) {
            System.out.println("New Major Must Be SE or SS Not: " + newMajor);
        } else {
            String oldMajor = newMajor;
            major = newMajor;
            System.out.println(name + " has changed Major From : " + oldMajor + " To: " + newMajor);
        }

    }

    public void registerCourse(String courseName) {
        System.out.println(name + " has registered for : " + courseName);
    }

    public void payFee(double amount) {
        System.out.println(name + " has paid of : " + amount);
    }

    void showInfo() {
        System.out.println("----------Student Infomation---------");
        System.out.println("Student ID: " + id);
        System.out.println("Student Name: " + name);
        System.out.println("Student birthYear: " + birthYear);
        System.out.println("Student major: " + major);
        System.out.println("Student Class: " + className);

    }

    @Override
    public String toString() {
        return "ID=" + id + ", NAME=" + name + ", birthYear=" + birthYear + ", major=" + major + ", className=" + className;
    }

}
