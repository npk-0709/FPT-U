package model;

/**
 * Lớp đại diện cho một Cầu thủ (Player).
 * Áp dụng Inheritance: kế thừa Person (id, name).
 * Áp dụng Polymorphism: override getDisplayInfo(), compareTo().
 */
public class Player extends Person implements Comparable<Player> {

    private String clubID;      // FK → Club
    private String position;    // Goalkeeper|Defender|Midfielder|Forward|Winger
    private int shirtNumber;    // 1–99, duy nhất trong cùng Club

    /**
     * Constructor khởi tạo Player.
     * @param id          mã cầu thủ (Pxxxx)
     * @param clubID      mã câu lạc bộ (CL-xxxx)
     * @param name        tên cầu thủ
     * @param position    vị trí thi đấu
     * @param shirtNumber số áo (1–99)
     */
    public Player(String id, String clubID, String name,
                  String position, int shirtNumber) {
        super(id, name);
        this.clubID = clubID;
        this.position = position;
        this.shirtNumber = shirtNumber;
    }

    // ===== Getter =====
    public String getClubID() {
        return clubID;
    }

    public String getPosition() {
        return position;
    }

    public int getShirtNumber() {
        return shirtNumber;
    }

    // ===== Setter =====
    public void setPosition(String position) {
        this.position = position;
    }

    public void setShirtNumber(int shirtNumber) {
        this.shirtNumber = shirtNumber;
    }

    // ===== Abstraction: override từ Person =====
    @Override
    public String getDisplayInfo() {
        return id + " - " + name + " (#" + shirtNumber + ")";
    }

    // ===== Comparable: sắp theo số áo (mặc định) =====
    @Override
    public int compareTo(Player o) {
        return Integer.compare(this.shirtNumber, o.shirtNumber);
    }

    // ===== toString: hiển thị dạng bảng =====
    @Override
    public String toString() {
        return String.format("%-7s| %-10s| %-22s| %-12s| %d",
                id, clubID, name, position, shirtNumber);
    }

    // ===== equals & hashCode: so sánh theo playerID =====
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
