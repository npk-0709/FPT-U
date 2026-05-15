import java.util.Locale;

public class StatisticalInfo {

    private String mountainCode;
    private String mountainName;
    private int numOfStudent;
    private double totalCost;

    public StatisticalInfo() {
    }

    public StatisticalInfo(String mountainCode, String mountainName,
                           int numOfStudent, double totalCost) {
        this.mountainCode = mountainCode;
        this.mountainName = mountainName;
        this.numOfStudent = numOfStudent;
        this.totalCost = totalCost;
    }

    public String getMountainCode() {
        return mountainCode;
    }

    public void setMountainCode(String mountainCode) {
        this.mountainCode = mountainCode;
    }

    public String getMountainName() {
        return mountainName;
    }

    public void setMountainName(String mountainName) {
        this.mountainName = mountainName;
    }

    public int getNumOfStudent() {
        return numOfStudent;
    }

    public void setNumOfStudent(int numOfStudent) {
        this.numOfStudent = numOfStudent;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void addStudent(double fee) {
        this.numOfStudent++;
        this.totalCost += fee;
    }

    @Override
    public String toString() {
        return String.format(Locale.US,
                "%-5s | %-25s | %-22d | %,15.0f",
                mountainCode, mountainName, numOfStudent, totalCost);
    }
}
