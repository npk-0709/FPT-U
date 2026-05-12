
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Khuong
 */
public class PlayerManager extends ArrayList<Player> {

    public Player searchById(String id) {
        int sizeOfList = size();
        for (int i = 0; i < sizeOfList; ++i) {
            Player currentPlayer = get(i);
            if (currentPlayer.getPlayerId().equalsIgnoreCase(id)) {
                return currentPlayer;
            }
        }
        return null;
    }

    public boolean addPlayer(Player p) {
        if (this.searchById(p.getPlayerId()) == null || p.getPlayerId().equals("")) {
            add(p);
            return true;
        }
        return false;
    }

    public boolean deletePlayer(String id) {
        if (this.searchById(id) == null) {
            return false;
        } else {
            this.remove(this.searchById(id));

        }
        return true;
    }

    public void displayAllPlayers() {
        int sizeOfList = size();
        for (int i = 0; i < sizeOfList; ++i) {
            Player currentPlayer = get(i);
            System.out.println(currentPlayer.toString());
        }
        if (sizeOfList == 0) {
            System.out.println("Player is empty !");
        }
    }
    
    
}
