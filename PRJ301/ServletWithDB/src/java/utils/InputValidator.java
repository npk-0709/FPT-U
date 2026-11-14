package utils; // Thay đổi package tùy theo cấu trúc project của bạn

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Thư viện Validate Input đầu vào cho ứng dụng Web (Servlet/JSP, Spring MVC, REST API).
 * Sử dụng pre-compiled Regex Pattern để tối ưu hóa hiệu năng bộ nhớ và CPU.
 */
public final class InputValidator {

    // Chặn khởi tạo đối tượng vì đây là lớp tiện ích (Utility Class)
    private InputValidator() {
        throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }

    // =========================================================================
    // 1. CÁC BIỂU THỨC CHÍNH QUY (PRE-COMPILED REGEX PATTERNS)
    // =========================================================================

    // Email chuẩn (hỗ trợ hầu hết các tên miền phổ biến và tên miền phụ)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // Số điện thoại Việt Nam (Đầu 03, 05, 07, 08, 09 hoặc +84, gồm 10 chữ số)
    private static final Pattern VN_PHONE_PATTERN = Pattern.compile(
            "^(0|\\+84)(3[2-9]|5[2689]|7[06-9]|8[1-9]|9[0-9])[0-9]{7}$"
    );

    // Username: Chỉ chữ cái, số, dấu gạch dưới (_), dấu chấm (.), từ 4 - 20 ký tự, không bắt đầu/kết thúc bằng dấu
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9](?!.*[_.]{2})[a-zA-Z0-9._]{2,18}[a-zA-Z0-9]$"
    );

    // Mật khẩu mạnh: Tối thiểu 8 ký tự, ít nhất 1 chữ hoa, 1 chữ thường, 1 chữ số và 1 ký tự đặc biệt
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()_~`-])(?=\\S+$).{8,64}$"
    );

    // Họ và tên Tiếng Việt (Hỗ trợ đầy đủ bảng mã Unicode dựng sẵn, cho phép khoảng trắng giữa các từ)
    private static final Pattern VIETNAMESE_NAME_PATTERN = Pattern.compile(
            "^[\\p{L}]+([\\s][\\p{L}]+)*$"
    );

    // Căn cước công dân Việt Nam (12 chữ số) hoặc CMND cũ (9 chữ số)
    private static final Pattern VN_ID_CARD_PATTERN = Pattern.compile(
            "^(\\d{9}|\\d{12})$"
    );

    // Mã sinh viên / Mã nhân viên chuẩn (VD: SE203056, HE150001, NV00123 - 2 chữ cái đầu + 4 đến 8 số)
    private static final Pattern CODE_ID_PATTERN = Pattern.compile(
            "^[A-Za-z]{2,4}\\d{4,8}$"
    );

    // Chỉ chứa chữ cái và số (Alphanumeric)
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9]+$"
    );

    // Kiểm tra các thẻ HTML / Script nguy hiểm (Chống XSS cơ bản)
    private static final Pattern XSS_PATTERN = Pattern.compile(
            "(?i)<script.*?>.*?</script.*?>|<.*?javascript:.*?>|<.*?on\\w+\\s*=.*?>|<iframe.*?>|<object.*?>"
    );


    // =========================================================================
    // 2. NHÓM VALIDATE CHUỖI CƠ BẢN (STRING & NULL CHECKS)
    // =========================================================================

    /**
     * Kiểm tra chuỗi khác null và không rỗng (sau khi đã cắt khoảng trắng 2 đầu).
     */
    public static boolean isNotBlank(String input) {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * Kiểm tra độ dài chuỗi (sau khi trim) nằm trong khoảng [min, max].
     */
    public static boolean isLengthValid(String input, int min, int max) {
        if (!isNotBlank(input)) return false;
        int len = input.trim().length();
        return len >= min && len <= max;
    }

    /**
     * Kiểm tra chuỗi chỉ chứa chữ cái và số (không khoảng trắng, không ký tự đặc biệt).
     */
    public static boolean isAlphanumeric(String input) {
        return isNotBlank(input) && ALPHANUMERIC_PATTERN.matcher(input.trim()).matches();
    }


    // =========================================================================
    // 3. NHÓM VALIDATE ĐỊNH DẠNG CÁ NHÂN & TÀI KHOẢN (USER & IDENTITY)
    // =========================================================================

    /**
     * Kiểm tra định dạng Email hợp lệ.
     */
    public static boolean isValidEmail(String email) {
        return isNotBlank(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Kiểm tra số điện thoại di động Việt Nam.
     */
    public static boolean isValidVNPhone(String phone) {
        return isNotBlank(phone) && VN_PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Kiểm tra tên đăng nhập (Username: 4-20 ký tự, không có ký tự đặc biệt liền nhau).
     */
    public static boolean isValidUsername(String username) {
        return isNotBlank(username) && USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Kiểm tra độ mạnh mật khẩu (Ít nhất 8 ký tự, gồm Hoa, thường, số, ký tự đặc biệt, không khoảng trắng).
     */
    public static boolean isStrongPassword(String password) {
        // Không dùng trim() cho password vì khoảng trắng ở đầu/cuối là không hợp lệ
        return password != null && STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Kiểm tra mật khẩu nhập lại (Confirm Password) có khớp với mật khẩu gốc không.
     */
    public static boolean isPasswordMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * Kiểm tra Họ tên Tiếng Việt (Không chứa số, không ký tự đặc biệt, độ dài từ 2 - 50 ký tự).
     */
    public static boolean isValidVietnameseName(String fullName) {
        if (!isLengthValid(fullName, 2, 50)) return false;
        return VIETNAMESE_NAME_PATTERN.matcher(fullName.trim()).matches();
    }

    /**
     * Kiểm tra số CCCD (12 số) hoặc CMND (9 số).
     */
    public static boolean isValidVNIdCard(String idCard) {
        return isNotBlank(idCard) && VN_ID_CARD_PATTERN.matcher(idCard.trim()).matches();
    }

    /**
     * Kiểm tra định dạng Mã số (VD: Mã SV, Mã NV như SE203056).
     */
    public static boolean isValidCodeId(String code) {
        return isNotBlank(code) && CODE_ID_PATTERN.matcher(code.trim()).matches();
    }


    // =========================================================================
    // 4. NHÓM VALIDATE SỐ HỌC & TÀI CHÍNH (NUMBERS & FINANCE)
    // =========================================================================

    /**
     * Kiểm tra chuỗi có phải là số nguyên (Integer) hợp lệ hay không.
     */
    public static boolean isInteger(String input) {
        if (!isNotBlank(input)) return false;
        try {
            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Kiểm tra chuỗi là số nguyên và nằm trong khoảng [min, max] (VD: Số lượng sản phẩm, Số trang).
     */
    public static boolean isIntInRange(String input, int min, int max) {
        if (!isInteger(input)) return false;
        int val = Integer.parseInt(input.trim());
        return val >= min && val <= max;
    }

    /**
     * Kiểm tra chuỗi có phải là số nguyên dương (> 0) hay không (Thường dùng cho ID, Quantity).
     */
    public static boolean isPositiveInt(String input) {
        if (!isInteger(input)) return false;
        return Integer.parseInt(input.trim()) > 0;
    }

    /**
     * Kiểm tra chuỗi có phải là số thực / tiền tệ hợp lệ và lớn hơn hoặc bằng 0 (Dùng cho Price, Amount).
     */
    public static boolean isValidPrice(String input) {
        if (!isNotBlank(input)) return false;
        try {
            BigDecimal price = new BigDecimal(input.trim());
            return price.compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // =========================================================================
    // 5. NHÓM VALIDATE NGÀY THÁNG & THỜI GIAN (DATE & TIME)
    // =========================================================================

    /**
     * Kiểm tra định dạng ngày tháng nghiêm ngặt (Chặn các ngày vô lý như 30/02 hoặc 31/04).
     * @param dateStr Chuỗi ngày tháng (VD: "2005-09-07" hoặc "07/09/2005")
     * @param pattern Định dạng tương ứng (VD: "uuuu-MM-dd" hoặc "dd/MM/uuuu" - Lưu ý dùng 'uuuu' thay vì 'yyyy' cho STRICT mode)
     */
    public static boolean isValidDate(String dateStr, String pattern) {
        if (!isNotBlank(dateStr) || !isNotBlank(pattern)) return false;
        try {
            // Thay thế yyyy thành uuuu để tương thích với ResolverStyle.STRICT của Java 8+
            String strictPattern = pattern.replace("y", "u");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(strictPattern)
                    .withResolverStyle(ResolverStyle.STRICT);
            LocalDate.parse(dateStr.trim(), formatter);
            return true;
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Kiểm tra ngày sinh và giới hạn độ tuổi (VD: Người dùng phải từ 18 đến 100 tuổi).
     * Mặc định nhận chuỗi định dạng chuẩn HTML5 input type="date" là "yyyy-MM-dd".
     */
    public static boolean isValidAge(String birthDateStr, int minAge, int maxAge) {
        if (!isValidDate(birthDateStr, "uuuu-MM-dd")) return false;
        LocalDate birthDate = LocalDate.parse(birthDateStr.trim());
        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today)) return false; // Ngày sinh không thể ở tương lai

        int age = Period.between(birthDate, today).getYears();
        return age >= minAge && age <= maxAge;
    }

    /**
     * Kiểm tra ngày kết thúc phải diễn ra sau (hoặc cùng ngày) với ngày bắt đầu (Dùng cho Booking, Khuyến mãi).
     */
    public static boolean isEndDateAfterStartDate(String startDateStr, String endDateStr, String pattern) {
        if (!isValidDate(startDateStr, pattern) || !isValidDate(endDateStr, pattern)) {
            return false;
        }
        String strictPattern = pattern.replace("y", "u");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(strictPattern);
        LocalDate start = LocalDate.parse(startDateStr.trim(), formatter);
        LocalDate end = LocalDate.parse(endDateStr.trim(), formatter);
        return !end.isBefore(start);
    }


    // =========================================================================
    // 6. NHÓM VALIDATE WEB, FILE UPLOAD & BẢO MẬT (WEB, FILE & SECURITY)
    // =========================================================================

    /**
     * Kiểm tra định dạng URL (Chỉ chấp nhận giao thức http và https).
     */
    public static boolean isValidUrl(String url) {
        if (!isNotBlank(url)) return false;
        try {
            URI uri = new URI(url.trim());
            String scheme = uri.getScheme();
            return scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    && uri.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra giá trị đầu vào có nằm trong danh sách cho phép (Whitelist) hay không.
     * Rất hữu ích khi validate các thẻ <select>, Radio Button, Status, Role (VD: "ADMIN", "USER", "STAFF").
     */
    public static boolean isInWhitelist(String input, Collection<String> allowedValues) {
        if (!isNotBlank(input) || allowedValues == null || allowedValues.isEmpty()) return false;
        return allowedValues.contains(input.trim());
    }

    /**
     * Kiểm tra đuôi file (Extension) khi người dùng upload ảnh/tài liệu.
     * @param fileName Tên file gốc (VD: "avatar.png")
     * @param allowedExtensions Tập hợp các đuôi cho phép (VD: Set.of("jpg", "jpeg", "png", "webp"))
     */
    public static boolean isValidFileExtension(String fileName, Set<String> allowedExtensions) {
        if (!isNotBlank(fileName) || allowedExtensions == null || allowedExtensions.isEmpty()) return false;
        String cleanName = fileName.trim();
        int lastDotIndex = cleanName.lastIndexOf('.');
        if (lastDotIndex <= 0 || lastDotIndex == cleanName.length() - 1) {
            return false; // Không có dấu chấm hoặc dấu chấm nằm ở đầu/cuối
        }
        String ext = cleanName.substring(lastDotIndex + 1).toLowerCase();
        return allowedExtensions.contains(ext);
    }

    /**
     * Kiểm tra kích thước file upload (tính bằng Bytes) không vượt quá giới hạn MB cho phép.
     */
    public static boolean isValidFileSize(long fileSizeInBytes, int maxSizeInMB) {
        long maxBytes = (long) maxSizeInMB * 1024 * 1024;
        return fileSizeInBytes > 0 && fileSizeInBytes <= maxBytes;
    }

    /**
     * Kiểm tra chuỗi có an toàn trước lỗi XSS cơ bản (Không chứa các thẻ <script>, sự kiện inline).
     */
    public static boolean isSafeFromXSS(String input) {
        if (input == null) return true;
        return !XSS_PATTERN.matcher(input).find();
    }

    /**
     * Làm sạch chuỗi (Sanitize HTML) để hiển thị an toàn lên JSP/HTML, tránh lỗi XSS.
     */
    public static String sanitizeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }


    // =========================================================================
    // 7. BỘ GOM LỖI TIỆN ÍCH CHO CONTROLLER / SERVLET (FLUENT VALIDATION RESULT)
    // =========================================================================

    /**
     * Class hỗ trợ gom nhiều lỗi cùng lúc để đẩy về giao diện (JSP / JSON API).
     */
    public static class ValidationResult {
        private final Map<String, String> errors = new HashMap<>();

        /**
         * Thêm điều kiện kiểm tra. Nếu isValid == false thì lưu thông báo lỗi vào field tương ứng.
         * (Chỉ lưu lỗi đầu tiên của mỗi field để tránh ghi đè thông báo).
         */
        public ValidationResult check(boolean isValid, String fieldName, String errorMessage) {
            if (!isValid && !errors.containsKey(fieldName)) {
                errors.put(fieldName, errorMessage);
            }
            return this;
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public Map<String, String> getErrors() {
            return errors;
        }

        public String getError(String fieldName) {
            return errors.get(fieldName);
        }
    }
}