/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Khuong
 */
public abstract class Player {

    private String playerId;
    private String fullName;
    private int age;
    private String nationality;
    private String clubName;
    private double baseSalary;

    public Player(String playerId, String fullName, int age, String nationality, String clubName, double baseSalary) {
        this.playerId = playerId;
        this.fullName = fullName;
        this.age = age;
        this.nationality = nationality;
        this.clubName = clubName;
        this.baseSalary = baseSalary;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        System.out.println("Can not change !");
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    @Override
    public String toString() {
        return  playerId + "," + fullName + "," + age + "," + nationality + "," + clubName + "," + baseSalary;
    }

    public abstract double calculateBonus();

    public double calculateTotalIncome() {
        return this.getBaseSalary() + this.calculateBonus();
    }

}
