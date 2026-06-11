package model;

import java.util.Objects;

public class Player implements IEntity {

    private String playerId;
    private String clubId;
    private String playerName;
    private String position;
    private int shirtNumber;

    public Player() {
    }

    public Player(String playerId, String clubId, String playerName, String position, int shirtNumber) {
        this.playerId = playerId;
        this.clubId = clubId;
        this.playerName = playerName;
        this.position = position;
        this.shirtNumber = shirtNumber;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getShirtNumber() {
        return shirtNumber;
    }

    public void setShirtNumber(int shirtNumber) {
        this.shirtNumber = shirtNumber;
    }

    @Override
    public void displayInfo() {
        System.out.printf("| %-10s | %-10s | %-25s | %-12s | %6d |\n",
                playerId, clubId, playerName, position, shirtNumber);
    }

    @Override
    public String toString() {
        return playerId + "," + clubId + "," + playerName + "," + position + "," + shirtNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(playerId, player.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
}
