package model;

import java.util.Objects;

public class Club implements IEntity {

    private String clubId;
    private String clubName;
    private String sponsorBrand;
    private double budget;

    public Club() {
    }

    public Club(String clubId, String clubName, String sponsorBrand, double budget) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.sponsorBrand = sponsorBrand;
        this.budget = budget;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getSponsorBrand() {
        return sponsorBrand;
    }

    public void setSponsorBrand(String sponsorBrand) {
        this.sponsorBrand = sponsorBrand;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    @Override
    public void displayInfo() {
        System.out.printf("| %-10s | %-25s | %-20s | %15.2f |\n",
                clubId, clubName, sponsorBrand, budget);
    }

    @Override
    public String toString() {
        return clubId + "," + clubName + "," + sponsorBrand + "," + budget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Club club = (Club) o;
        return Objects.equals(clubId, club.clubId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clubId);
    }
}
