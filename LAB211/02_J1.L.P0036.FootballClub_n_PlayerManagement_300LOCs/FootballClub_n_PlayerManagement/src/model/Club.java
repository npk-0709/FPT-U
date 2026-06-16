package model;

import java.io.Serializable;

/**
 * Lớp đại diện cho một Câu lạc bộ bóng đá (Club).
 * Áp dụng Encapsulation: field private + getter/setter.
 * Implements Comparable để sắp xếp theo tên club.
 */
public class Club implements Serializable, Comparable<Club> {

    private String clubID;      // CL-xxxx
    private String name;        // tên câu lạc bộ
    private String sponsor;     // nhãn hiệu tài trợ
    private double budget;      // ngân sách (triệu EUR)

    /**
     * Constructor khởi tạo Club.
     * @param clubID  mã câu lạc bộ (CL-xxxx)
     * @param name    tên câu lạc bộ
     * @param sponsor nhãn hiệu tài trợ
     * @param budget  ngân sách (triệu EUR, phải dương)
     */
    public Club(String clubID, String name, String sponsor, double budget) {
        this.clubID = clubID;
        this.name = name;
        this.sponsor = sponsor;
        this.budget = budget;
    }

    // ===== Getter =====
    public String getClubID() {
        return clubID;
    }

    public String getName() {
        return name;
    }

    public String getSponsor() {
        return sponsor;
    }

    public double getBudget() {
        return budget;
    }

    // ===== Setter (có validate) =====
    public void setName(String name) {
        this.name = name;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    /**
     * Set budget — chỉ chấp nhận giá trị dương.
     * @param budget giá trị mới (triệu EUR)
     */
    public void setBudget(double budget) {
        if (budget > 0) {
            this.budget = budget;
        }
    }

    // ===== Comparable: sắp theo tên club (case-insensitive) =====
    @Override
    public int compareTo(Club o) {
        return this.name.compareToIgnoreCase(o.name);
    }

    // ===== toString: hiển thị dạng bảng =====
    @Override
    public String toString() {
        return String.format("%-10s| %-26s| %-10s| %,.0f",
                clubID, name, sponsor, budget);
    }

    // ===== equals & hashCode: so sánh theo clubID =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Club club = (Club) o;
        return clubID.equals(club.clubID);
    }

    @Override
    public int hashCode() {
        return clubID.hashCode();
    }
}
