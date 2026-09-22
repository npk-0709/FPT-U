import java.util.regex.Pattern;

public interface Acceptable {

    String STUDENT_ID = "^(?i)(SE|HE|DE|QE|CE)\\d{6}$";
    String CAMPUS_CODE = "^(?i)(SE|HE|DE|QE|CE)$";
    String NAME_VALID = "^[A-Za-zÀ-ỹ\\s]{2,20}$";
    String DOUBLE_VALID = "^[0-9]*\\.?[0-9]+$";
    String INTEGER_VALID = "^\\d+$";
    String PHONE_VALID = "^0\\d{9}$";
    String VIETTEL_VALID = "^(032|033|034|035|036|037|038|039|096|097|098|086)\\d{7}$";
    String VNPT_VALID = "^(081|082|083|084|085|088|091|094)\\d{7}$";
    String EMAIL_VALID = "^[A-Za-z0-9+_.-]@+[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    String YES_NO_VALID = "^[YyNn]$";
    String MENU_VALID = "^[1-9]$";

    // Volunteer patterns
    String VOLUNTEER_ID = "^(?i)VL\\d{3}$";
    String VOLUNTEER_NAME_VALID = "^[A-Za-zÀ-ỹ\\s]{3,30}$";
    String SHIFT_VALID = "^[1-3]$";

    // Account patterns
    String USERNAME_VALID = "^[A-Za-z0-9_]{3,20}$";
    String PASSWORD_VALID = "^\\S{6,20}$";

    static boolean isValid(String data, String pattern) {
        if (data == null || pattern == null) {
            return false;
        }
        return Pattern.matches(pattern, data);
    }
}
