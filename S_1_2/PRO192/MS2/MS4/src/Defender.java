/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Khuong
 */
public class Defender extends Player {

    private int tackles;

    public Defender(String playerId, String fullName, int age, String nationality, String clubName, double baseSalary, int tackles) {
        super(playerId, fullName, age, nationality, clubName, baseSalary);
        this.tackles = tackles;
    }

    public int getTackles() {
        return tackles;
    }

    public void setTackles(int tackles) {
        this.tackles = tackles;
    }

    @Override
    public double calculateBonus() {
        return tackles * 50;
    }

    @Override
    public String toString() {
        return "Defender," + super.toString() + "," + this.getTackles();
    }

}
