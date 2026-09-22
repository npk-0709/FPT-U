/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Khuong
 */
public class Forward extends Player {

    private int goals;

    public Forward(String playerId, String fullName, int age, String nationality, String clubName, double baseSalary, int goals) {
        super(playerId, fullName, age, nationality, clubName, baseSalary);
        this.goals = goals;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    @Override
    public double calculateBonus() {
        return goals * 300; // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

     @Override
    public String toString() {
        return "Forward=" + super.toString() + ", Goals=" + this.getGoals();
    }


}
