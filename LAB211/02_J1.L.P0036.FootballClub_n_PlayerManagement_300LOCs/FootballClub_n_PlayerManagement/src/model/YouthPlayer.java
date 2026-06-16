package model;

/**
 * Lớp đại diện cho một Cầu thủ trẻ (YouthPlayer).
 * Áp dụng Inheritance: kế thừa Person (id, name).
 * Áp dụng Polymorphism: override getDisplayInfo(), toString().
 * Áp dụng Computational Thinking:
 *   - Quyết định: suy ra đề xuất lên đội 1 từ tuổi (age >= 18).
 *   - Kiểm chứng: age phải nằm trong [8, 21].
 */
public class YouthPlayer extends Person implements Comparable<YouthPlayer> {

    private String clubId;  // FK → Club
    private int age;        // 8..21

    /**
     * Constructor khởi tạo YouthPlayer.
     * @param id     mã cầu thủ trẻ (AC-xxxx)
     * @param clubId mã câu lạc bộ (CL-xxxx)
     * @param name   tên cầu thủ trẻ
     * @param age    tuổi (8–21)
     */
    public YouthPlayer(String id, String clubId, String name, int age) {
        super(id, name);
        this.clubId = clubId;
        this.age = age;
    }

    // ===== Getter =====
    public String getClubId() {
        return clubId;
    }

    public int getAge() {
        return age;
    }

    // ===== Setter =====
    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // ===== Computational Thinking: Quyết định =====
    /**
     * Suy luận cầu thủ trẻ có đủ tuổi lên đội 1 hay không.
     * Ngưỡng: age >= 18.
     * @return true nếu đủ tuổi lên đội 1
     */
    public boolean isEligibleForFirstTeam() {
        return age >= Validatable.FIRST_TEAM_AGE;
    }

    // ===== Abstraction: override từ Person =====
    @Override
    public String getDisplayInfo() {
        String info = id + " - " + name + " (Age: " + age + ")";
        if (isEligibleForFirstTeam()) {
            info += " [★ FIRST TEAM ELIGIBLE]";
        }
        return info;
    }

    // ===== Comparable: sắp theo tên (mặc định) =====
    @Override
    public int compareTo(YouthPlayer o) {
        return this.name.compareToIgnoreCase(o.name);
    }

    // ===== toString: hiển thị dạng bảng =====
    @Override
    public String toString() {
        String eligibility = isEligibleForFirstTeam() ? "Yes" : "No";
        return String.format("%-8s| %-10s| %-22s| %-4d| %s",
                id, clubId, name, age, eligibility);
    }

    // ===== equals & hashCode: so sánh theo id =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YouthPlayer that = (YouthPlayer) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
