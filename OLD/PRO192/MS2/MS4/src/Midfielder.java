/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Khuong
 */
public class Midfielder extends Player {

    private int assists;

    public Midfielder(String playerId, String fullName, int age, String nationality, String clubName, double baseSalary, int assists) {
        super(playerId, fullName, age, nationality, clubName, baseSalary);
        this.assists = assists;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    @Override
    public double calculateBonus() {
        return assists * 150; // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public String toString() {
        return "Midfielder," + super.toString() + "," + this.getAssists();
    }

}
