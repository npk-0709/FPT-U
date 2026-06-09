import java.util.Locale;
import java.util.Objects;

public class Student extends Person implements Comparable<Student> {

    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_FEE = 6_000_000;
    public static final double DISCOUNT_RATE = 0.35;

    private String phone;
    private String email;
    private String mountainCode;
    private double tuitionFee;

    public Student() {
    }

    public Student(String id, String name, String phone, String email,
                   String mountainCode, double tuitionFee) {
        super(id, name);
        this.phone = phone;
        this.email = email;
        this.mountainCode = mountainCode;
        this.tuitionFee = tuitionFee;
    }

    @Override
    public String getDisplayInfo() {
        return toString();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.tuitionFee = calculateFee(phone);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMountainCode() {
        return mountainCode;
    }

    public void setMountainCode(String mountainCode) {
        this.mountainCode = mountainCode;
    }

    public double getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(double tuitionFee) {
        this.tuitionFee = tuitionFee;
    }

    public String getCampusCode() {
        if (id == null || id.length() < 2) {
            return "";
        }
        return id.substring(0, 2).toUpperCase();
    }

    public static double calculateFee(String phone) {
        if (Acceptable.isValid(phone, Acceptable.VIETTEL_VALID)
                || Acceptable.isValid(phone, Acceptable.VNPT_VALID)) {
            return DEFAULT_FEE * (1 - DISCOUNT_RATE);
        }
        return DEFAULT_FEE;
    }

    @Override
    public String toString() {
        return String.format(Locale.US,
                "%-10s | %-20s | %-12s | %-25s | %-9s | %,12.0f",
                id, name, phone, email, mountainCode, tuitionFee);
    }

    public String toCsv() {
        return csv(id) + "," + csv(name) + "," + csv(phone) + ","
                + csv(email) + "," + csv(mountainCode) + ","
                + String.format(Locale.US, "%.0f", tuitionFee);
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    @Override
    public int compareTo(Student other) {
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
        if (!(obj instanceof Student)) {
            return false;
        }
        Student other = (Student) obj;
        return id != null && other.id != null && id.equalsIgnoreCase(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id == null ? "" : id.toUpperCase());
    }
}
