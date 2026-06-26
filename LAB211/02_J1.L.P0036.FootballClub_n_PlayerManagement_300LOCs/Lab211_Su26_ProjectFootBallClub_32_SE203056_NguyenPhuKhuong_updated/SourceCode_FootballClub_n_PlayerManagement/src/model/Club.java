package model;

import java.io.Serializable;

public class Club implements Serializable, Comparable<Club> {

    private String clubID;
    private String name;
    private String sponsor;
    private double budget;

    public Club(String clubID, String name, String sponsor, double budget) {
        this.clubID = clubID;
        this.name = name;
        this.sponsor = sponsor;
        this.budget = budget;
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public void setBudget(double budget) {
        if (budget > 0) {
            this.budget = budget;
        }
    }

    @Override
    public int compareTo(Club o) {
        return this.name.compareToIgnoreCase(o.name);
    }

    @Override
    public String toString() {
        return String.format("%-10s| %-26s| %-10s| %,.0f",
                clubID, name, sponsor, budget);
    }

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
