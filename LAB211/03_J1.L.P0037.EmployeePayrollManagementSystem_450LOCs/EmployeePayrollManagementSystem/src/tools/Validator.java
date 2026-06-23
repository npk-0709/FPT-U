package tools;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Tập trung toàn bộ quy tắc hợp lệ (validation) của đề bài.
 * Gom về một nơi để tái dùng giữa load (Function 1) và add/update (2, 3).
 */
public final class Validator {

    /** ID: chữ E in hoa theo sau là đúng 3 chữ số, ví dụ E001. */
    public static final String ID_REGEX = "E\\d{3}";

    public static final List<String> ROLES =
            Arrays.asList("Developer", "Tester", "Manager", "HR");

    public static final List<String> STATUSES =
            Arrays.asList("active", "inactive");

    private static final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);

    private Validator() {
    }

    public static boolean isEmployeeId(String id) {
        return id != null && ID_PATTERN.matcher(id.trim()).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isRole(String role) {
        if (role == null) {
            return false;
        }
        for (String r : ROLES) {
            if (r.equalsIgnoreCase(role.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStatus(String status) {
        if (status == null) {
            return false;
        }
        for (String s : STATUSES) {
            if (s.equalsIgnoreCase(status.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPositive(double value) {
        return value > 0;
    }

    public static boolean isWorkingDays(int days) {
        return days >= 0 && days <= 26;
    }

    public static boolean isNonNegative(double value) {
        return value >= 0;
    }
}
