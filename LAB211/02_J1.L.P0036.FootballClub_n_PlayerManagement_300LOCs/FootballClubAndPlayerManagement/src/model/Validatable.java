package model;

import java.util.Set;

/**
 * Interface chứa các hằng số validation và phương thức kiểm tra hợp lệ.
 * Áp dụng Pattern Recognition: tập trung quy tắc validate tại một nơi duy nhất.
 */
public interface Validatable {

    // Regex patterns cho ID
    String CLUB_ID_REGEX   = "^CL-\\d{4}$";   // CL-0001, CL-9999
    String PLAYER_ID_REGEX = "^P\\d{4}$";      // P0001, P9999

    // Tập hợp các vị trí hợp lệ (lowercase để so sánh case-insensitive)
    Set<String> POSITIONS = Set.of(
            "goalkeeper", "defender", "midfielder", "forward", "winger"
    );

    /**
     * Kiểm tra giá trị có khớp pattern hay không.
     * @param value   chuỗi cần kiểm tra
     * @param pattern regex pattern
     * @return true nếu hợp lệ
     */
    static boolean isValid(String value, String pattern) {
        return value != null && value.matches(pattern);
    }

    /**
     * Kiểm tra vị trí cầu thủ có hợp lệ hay không (case-insensitive).
     * @param value vị trí cần kiểm tra
     * @return true nếu thuộc tập POSITIONS
     */
    static boolean isPosition(String value) {
        return value != null && POSITIONS.contains(value.toLowerCase());
    }
}
