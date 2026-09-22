package model;

public class Player extends Person implements Comparable<Player> {

    private String clubID;
    private String position;
    private int shirtNumber;

    public Player(String id, String clubID, String name,
                  String position, int shirtNumber) {
        super(id, name);
        this.clubID = clubID;
        this.position = position;
        this.shirtNumber = shirtNumber;
    }

    public String getClubID() {
        return clubID;
    }

    public String getPosition() {
        return position;
    }

    public int getShirtNumber() {
        return shirtNumber;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setShirtNumber(int shirtNumber) {
        this.shirtNumber = shirtNumber;
    }

    @Override
    public String getDisplayInfo() {
        return id + " - " + name + " (#" + shirtNumber + ")";
    }

    @Override
    public int compareTo(Player o) {
        return Integer.compare(this.shirtNumber, o.shirtNumber);
    }

    @Override
    public String toString() {
        return String.format("%-7s| %-10s| %-22s| %-12s| %d",
                id, clubID, name, position, shirtNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
