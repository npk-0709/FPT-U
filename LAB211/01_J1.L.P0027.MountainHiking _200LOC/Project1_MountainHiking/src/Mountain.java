import java.util.Objects;

public class Mountain {

    private String mountainCode;
    private String mountain;
    private String province;
    private String description;

    public Mountain() {
    }

    public Mountain(String mountainCode, String mountain, String province, String description) {
        this.mountainCode = mountainCode;
        this.mountain = mountain;
        this.province = province;
        this.description = description;
    }

    public String getMountainCode() {
        return mountainCode;
    }

    public void setMountainCode(String mountainCode) {
        this.mountainCode = mountainCode;
    }

    public String getMountain() {
        return mountain;
    }

    public void setMountain(String mountain) {
        this.mountain = mountain;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%-5s | %-25s | %-15s | %s",
                mountainCode, mountain, province,
                description == null ? "" : description);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Mountain)) {
            return false;
        }
        Mountain other = (Mountain) obj;
        return mountainCode != null && other.mountainCode != null
                && mountainCode.equalsIgnoreCase(other.mountainCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mountainCode == null ? "" : mountainCode.toUpperCase());
    }
}
