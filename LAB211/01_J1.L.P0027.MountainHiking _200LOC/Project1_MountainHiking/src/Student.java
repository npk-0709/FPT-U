import java.io.Serializable;
import java.util.Locale;

public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_FEE = 6_000_000;
    public static final double DISCOUNT_RATE = 0.35;

    private String id;
    private String name;
    private String phone;
    private String email;
    private String mountainCode;
    private double tuitionFee;

    public Student() {
    }

    public Student(String id, String name, String phone, String email,
                   String mountainCode, double tuitionFee) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.mountainCode = mountainCode;
        this.tuitionFee = tuitionFee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return id + "," + name + "," + phone + "," + email + ","
                + mountainCode + "," + tuitionFee;
    }
}
