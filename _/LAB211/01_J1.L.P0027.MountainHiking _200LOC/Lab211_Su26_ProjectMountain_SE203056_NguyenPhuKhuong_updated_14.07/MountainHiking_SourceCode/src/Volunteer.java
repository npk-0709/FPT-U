import java.util.Locale;
import java.util.Objects;

public class Volunteer extends Person implements Comparable<Volunteer> {

    private static final long serialVersionUID = 2L;

    private Skill skill;
    private int maxShiftsPerDay; // 1..3
    private int shiftsToday;    // so ca da nhan hom nay

    public Volunteer() {
    }

    public Volunteer(String id, String name, Skill skill, int maxShiftsPerDay) {
        super(id, name);
        this.skill = skill;
        this.maxShiftsPerDay = maxShiftsPerDay;
        this.shiftsToday = 0;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public int getMaxShiftsPerDay() {
        return maxShiftsPerDay;
    }

    public void setMaxShiftsPerDay(int maxShiftsPerDay) {
        this.maxShiftsPerDay = maxShiftsPerDay;
    }

    public int getShiftsToday() {
        return shiftsToday;
    }

    public void setShiftsToday(int shiftsToday) {
        this.shiftsToday = shiftsToday;
    }


    public boolean assign() {
        if (shiftsToday >= maxShiftsPerDay) {
            return false;
        }
        shiftsToday++;
        return true;
    }


    public boolean hasSkillFor(Skill requiredSkill) {
        if (requiredSkill == null) {
            return true; // slot GENERAL, ai cũng được
        }
        return this.skill == requiredSkill;
    }

    @Override
    public String getDisplayInfo() {
        return String.format(Locale.US,
                "%-8s | %-25s | %-14s | max=%d | today=%d",
                id, name, skill, maxShiftsPerDay, shiftsToday);
    }

    @Override
    public String toString() {
        return getDisplayInfo();
    }

    public String toCsv() {
        return csv(id) + "," + csv(name) + ","
                + (skill == null ? "" : skill.name()) + ","
                + maxShiftsPerDay + "," + shiftsToday;
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    @Override
    public int compareTo(Volunteer other) {
        if (other == null) {
            return 1;
        }
        String thisId = id == null ? "" : id;
        String otherId = other.id == null ? "" : other.id;
        return thisId.compareToIgnoreCase(otherId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Volunteer)) {
            return false;
        }
        Volunteer other = (Volunteer) obj;
        return id != null && other.id != null && id.equalsIgnoreCase(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id == null ? "" : id.toUpperCase());
    }
}
