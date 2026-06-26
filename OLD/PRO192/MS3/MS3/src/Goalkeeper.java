/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Khuong
 */
public class Goalkeeper extends Player {

    private int cleanSheets;

    public Goalkeeper(String playerId, String fullName, int age, String nationality, String clubName, double baseSalary, int cleanSheets) {
        super(playerId, fullName, age, nationality, clubName, baseSalary);
        this.cleanSheets = cleanSheets;

    }

    public int getCleanSheets() {
        return cleanSheets;
    }

    public void setCleanSheets(int cleanSheets) {
        this.cleanSheets = cleanSheets;
    }

    @Override
    public double calculateBonus() {
        return cleanSheets * 200; // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public String toString() {
        return "Goalkeeper=" + super.toString() + ", CleanSheets=" + this.getCleanSheets();
    }

}
