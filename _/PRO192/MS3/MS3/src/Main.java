/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author Khuong
 */
public class Main {

    public static void main(String[] args) {

        Goalkeeper goalkeeper = new Goalkeeper("01", "NVA", 30, "VN", "u30", 500000.0, 3);
        Defender defender = new Defender("02", "NVB", 31, "VN", "u31", 600000.0, 2);
        Midfielder midfielder = new Midfielder("03", "NVC", 32, "V2", "u30", 700000.0, 4);
        Forward forward = new Forward("04", "NVD", 33, "VN", "u33", 800000.0, 5);
        PlayerManager playermanager = new PlayerManager();
        playermanager.addPlayer(goalkeeper);
        playermanager.addPlayer(defender);
        playermanager.addPlayer(midfielder);
        playermanager.addPlayer(forward);
        
        playermanager.displayAllPlayers();
        
        System.out.println(goalkeeper.calculateTotalIncome());
        System.out.println(defender.calculateTotalIncome());
        System.out.println(midfielder.calculateTotalIncome());
        System.out.println(forward.calculateTotalIncome());
    }

}
