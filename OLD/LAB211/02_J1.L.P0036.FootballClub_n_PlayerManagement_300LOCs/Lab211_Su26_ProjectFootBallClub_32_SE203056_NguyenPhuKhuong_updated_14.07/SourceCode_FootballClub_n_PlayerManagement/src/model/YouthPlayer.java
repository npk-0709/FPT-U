package model;

public class YouthPlayer extends Person implements Comparable<YouthPlayer> {

    private String clubId;
    private int age;

    public YouthPlayer(String id, String clubId, String name, int age) {
        super(id, name);
        this.clubId = clubId;
        this.age = age;
    }

    public String getClubId() {
        return clubId;
    }

    public int getAge() {
        return age;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isEligibleForFirstTeam() {
        return age >= Validatable.FIRST_TEAM_AGE;
    }

    @Override
    public String getDisplayInfo() {
        String info = id + " - " + name + " (Age: " + age + ")";
        if (isEligibleForFirstTeam()) {
            info += " [★ FIRST TEAM ELIGIBLE]";
        }
        return info;
    }

    @Override
    public int compareTo(YouthPlayer o) {
        return this.name.compareToIgnoreCase(o.name);
    }

    @Override
    public String toString() {
        String eligibility = isEligibleForFirstTeam() ? "Yes" : "No";
        return String.format("%-8s| %-10s| %-22s| %-4d| %s",
                id, clubId, name, age, eligibility);
    }

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
