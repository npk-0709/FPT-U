import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class XuLyDuLieuJava {

    public static void main(String[] args) {
        System.out.println("=== 1. XỬ LÝ CHUỖI CƠ BẢN ===");
        xuLyChuoiCoBan();

        System.out.println("\n=== 2. XỬ LÝ CHUỖI NÂNG CAO (REGEX & HIỆU SUẤT) ===");
        xuLyChuoiNangCao();

        System.out.println("\n=== 3. XỬ LÝ SỐ CƠ BẢN (CHUYỂN ĐỔI & ĐỊNH DẠNG) ===");
        xuLySoCoBan();

        System.out.println("\n=== 4. XỬ LÝ SỐ NÂNG CAO (SỐ LỚN & CHÍNH XÁC CAO) ===");
        xuLySoNangCao();

        System.out.println("\n=== 5. LỌC VÀ TÌM KIẾM TRONG DANH SÁCH (STREAM API) ===");
        locVaTimKiemDanhSach();
    }

    // ---------------------------------------------------------
    // 1. XỬ LÝ CHUỖI CƠ BẢN
    // ---------------------------------------------------------
    public static void xuLyChuoiCoBan() {
        String text = "   Xin chào, tôi là lập trình viên Java!   ";

        // Cắt khoảng trắng hai đầu và lấy độ dài
        String trimmedText = text.trim();
        System.out.println("Chuỗi gốc: '" + text + "'");
        System.out.println("Trimmed: '" + trimmedText + "' (Độ dài: " + trimmedText.length() + ")");

        // Cắt chuỗi (Substring)
        String sub = trimmedText.substring(0, 8); // Cắt từ index 0 đến 7
        System.out.println("Substring(0, 8): " + sub);

        // Thay thế (Replace)
        String replaced = trimmedText.replace("Java", "Python");
        System.out.println("Replace: " + replaced);

        // Chuyển đổi chữ hoa / chữ thường
        System.out.println("Uppercase: " + trimmedText.toUpperCase());
        System.out.println("Lowercase: " + trimmedText.toLowerCase());

        // Tìm kiếm vị trí (indexOf, contains)
        System.out.println("Chứa từ 'lập trình': " + trimmedText.contains("lập trình"));
        System.out.println("Vị trí chữ 'J': " + trimmedText.indexOf("J"));

        // Chia chuỗi thành mảng (Split)
        String[] words = trimmedText.split(" ");
        System.out.println("Split theo dấu cách: " + Arrays.toString(words));
    }

    // ---------------------------------------------------------
    // 2. XỬ LÝ CHUỖI NÂNG CAO (REGEX & BUILDER)
    // ---------------------------------------------------------
    public static void xuLyChuoiNangCao() {
        // a. Sử dụng StringBuilder cho hiệu suất cao khi nối chuỗi trong vòng lặp
        StringBuilder sb = new StringBuilder();
        sb.append("Java ");
        sb.append("rất ");
        sb.append("mạnh mẽ.");
        // Chèn thêm vào giữa
        sb.insert(9, "linh hoạt và ");
        System.out.println("StringBuilder: " + sb.toString());

        // b. Biểu thức chính quy (Regex) - Tìm kiếm và trích xuất dữ liệu phức tạp
        String data = "Liên hệ với tôi qua email: nvamai@gmail.com hoặc số ĐT: 0912345678. Email phụ: test@yahoo.vn";
        
        // Regex tìm Email
        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(data);

        System.out.println("Các email tìm thấy:");
        while (emailMatcher.find()) {
            System.out.println(" - " + emailMatcher.group());
        }

        // Regex tìm số điện thoại (10 số, bắt đầu bằng số 0)
        String phoneRegex = "0\\d{9}";
        Pattern phonePattern = Pattern.compile(phoneRegex);
        Matcher phoneMatcher = phonePattern.matcher(data);
        
        System.out.println("Các số điện thoại tìm thấy:");
        while (phoneMatcher.find()) {
            System.out.println(" - " + phoneMatcher.group());
        }

        // c. Xóa tất cả các ký tự không phải là số (Lọc số từ chuỗi)
        String mixedStr = "Giá của sản phẩm là 1.500.000 VNĐ";
        String numbersOnly = mixedStr.replaceAll("[^0-9]", "");
        System.out.println("Lọc riêng số từ chuỗi: " + numbersOnly);
    }

    // ---------------------------------------------------------
    // 3. XỬ LÝ SỐ CƠ BẢN
    // ---------------------------------------------------------
    public static void xuLySoCoBan() {
        String numStr1 = "100";
        String numStr2 = "45.67";
        String invalidNum = "100abc";

        // Ép kiểu từ Chuỗi sang Số
        try {
            int i = Integer.parseInt(numStr1);
            double d = Double.parseDouble(numStr2);
            System.out.println("ParseInt: " + i + ", ParseDouble: " + d);

            // Bắt lỗi khi ép kiểu chuỗi không hợp lệ
            int error = Integer.parseInt(invalidNum);
        } catch (NumberFormatException e) {
            System.out.println("Lỗi ép kiểu: Không thể chuyển '" + invalidNum + "' thành số.");
        }

        // Ép kiểu từ Số sang Chuỗi
        int myAge = 25;
        String ageStr = String.valueOf(myAge);
        
        // Định dạng số hiển thị (Thêm dấu phẩy hàng nghìn, làm tròn)
        double price = 1234567.899;
        DecimalFormat df = new DecimalFormat("#,###.00");
        System.out.println("Định dạng DecimalFormat: " + df.format(price));

        // Định dạng tiền tệ theo Locale
        NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        System.out.println("Định dạng Tiền tệ VNĐ: " + vnFormat.format(price));
    }

    // ---------------------------------------------------------
    // 4. XỬ LÝ SỐ NÂNG CAO (BIG INTEGER & BIG DECIMAL)
    // ---------------------------------------------------------
    public static void xuLySoNangCao() {
        // a. BigInteger - Dùng cho số nguyên khổng lồ vượt quá giới hạn của kiểu 'long'
        BigInteger big1 = new BigInteger("12345678901234567890");
        BigInteger big2 = new BigInteger("98765432109876543210");
        BigInteger sum = big1.add(big2);
        BigInteger multiply = big1.multiply(big2);
        
        System.out.println("BigInteger Cộng: " + sum);
        System.out.println("BigInteger Nhân: " + multiply);

        // b. BigDecimal - Dùng cho tính toán tiền tệ, số thập phân yêu cầu độ chính xác tuyệt đối
        // (Tránh lỗi sai số của double/float như 0.1 + 0.2 = 0.30000000000000004)
        BigDecimal bd1 = new BigDecimal("0.1");
        BigDecimal bd2 = new BigDecimal("0.2");
        System.out.println("Lỗi của double (0.1 + 0.2): " + (0.1 + 0.2));
        System.out.println("BigDecimal chính xác (0.1 + 0.2): " + bd1.add(bd2));

        // Chia số với BigDecimal (cần chỉ định quy tắc làm tròn nếu chia ra số vô tỉ)
        BigDecimal total = new BigDecimal("10");
        BigDecimal parts = new BigDecimal("3");
        BigDecimal division = total.divide(parts, 4, RoundingMode.HALF_UP); // Làm tròn 4 chữ số
        System.out.println("BigDecimal Chia (Làm tròn 4 số): " + division);
    }

    // ---------------------------------------------------------
    // 5. LỌC VÀ TÌM KIẾM TRONG DANH SÁCH BẰNG STREAM API
    // ---------------------------------------------------------
    public static void locVaTimKiemDanhSach() {
        List<String> danhSachTen = Arrays.asList("Nguyễn Văn A", "Trần Thị B", "Lê Văn C", "Nguyễn Hải D", "apple123");

        // Tìm tất cả những người họ "Nguyễn"
        List<String> hoNguyen = danhSachTen.stream()
                .filter(ten -> ten.startsWith("Nguyễn"))
                .collect(Collectors.toList());
        System.out.println("Lọc họ Nguyễn: " + hoNguyen);

        // Lọc các chuỗi có chứa số
        List<String> chuaSo = danhSachTen.stream()
                .filter(str -> str.matches(".*\\d.*")) // Regex: chứa ít nhất 1 chữ số
                .collect(Collectors.toList());
        System.out.println("Lọc chuỗi chứa số: " + chuaSo);

        // Xử lý list số: Chuyển chuỗi thành số, lọc số lớn hơn 20, tính tổng
        List<String> danhSachSoStr = Arrays.asList("10", "25", "5", "50", "abc", "30");
        
        int tong = danhSachSoStr.stream()
                .filter(s -> s.matches("-?\\d+")) // Lọc bỏ chữ, chỉ lấy số nguyên hợp lệ
                .map(Integer::parseInt)           // Chuyển String -> Integer
                .filter(n -> n > 20)              // Lọc các số > 20 (25, 50, 30)
                .reduce(0, Integer::sum);         // Tính tổng
                
        System.out.println("Tổng các số > 20 trong danh sách: " + tong);
    }
}


import java.util.Arrays;
import java.util.stream.Stream;

public class StringMethodsReference {

    public static void main(String[] args) {
        System.out.println("=== TỪ ĐIỂN TOÀN TẬP PHƯƠNG THỨC STRING TRONG JAVA ===\n");
        
        kiemTraVaThongTinCoBan();
        trichXuatKyTuVaMang();
        soSanhChuoi();
        timKiemVaKiemTra();
        thaoTacVaBienDoi();
        catVaNoiChuoi();
        chuyenDoiVaDinhDang();
        phuongThucNangCaoModernJava();
    }

    private static void kiemTraVaThongTinCoBan() {
        System.out.println("--- 1. Thông tin cơ bản ---");
        String str = "  Hello Java  ";
        
        // length(): Độ dài chuỗi
        System.out.println("length: " + str.length()); 
        
        // isEmpty(): Kiểm tra chuỗi rỗng ("")
        System.out.println("isEmpty: " + str.isEmpty()); 
        
        // isBlank(): Kiểm tra chuỗi rỗng hoặc chỉ chứa khoảng trắng (Java 11+)
        System.out.println("isBlank: " + str.isBlank()); 
    }

    private static void trichXuatKyTuVaMang() {
        System.out.println("\n--- 2. Trích xuất ký tự và mảng ---");
        String str = "Java";
        
        // charAt(index): Lấy ký tự tại vị trí
        System.out.println("charAt(1): " + str.charAt(1)); 
        
        // codePointAt(index): Lấy mã Unicode (ASCII) của ký tự
        System.out.println("codePointAt(0): " + str.codePointAt(0)); 
        
        // toCharArray(): Chuyển chuỗi thành mảng ký tự
        char[] chars = str.toCharArray();
        System.out.println("toCharArray: " + Arrays.toString(chars));
        
        // getBytes(): Chuyển thành mảng byte
        byte[] bytes = str.getBytes();
        System.out.println("getBytes: " + Arrays.toString(bytes));
    }

    private static void soSanhChuoi() {
        System.out.println("\n--- 3. So sánh chuỗi ---");
        String s1 = "java";
        String s2 = "JAVA";
        
        // equals(): So sánh phân biệt hoa thường
        System.out.println("equals: " + s1.equals(s2)); 
        
        // equalsIgnoreCase(): So sánh không phân biệt hoa thường
        System.out.println("equalsIgnoreCase: " + s1.equalsIgnoreCase(s2)); 
        
        // compareTo(): So sánh theo từ điển (trả về 0 nếu bằng, <0 nếu s1 đứng trước s2, >0 nếu s1 đứng sau)
        System.out.println("compareTo: " + s1.compareTo(s2)); 
        
        // compareToIgnoreCase(): So sánh theo từ điển không phân biệt hoa thường
        System.out.println("compareToIgnoreCase: " + s1.compareToIgnoreCase(s2)); 
    }

    private static void timKiemVaKiemTra() {
        System.out.println("\n--- 4. Tìm kiếm và Kiểm tra ---");
        String text = "Programming in Java is fun";
        
        // contains(): Kiểm tra chuỗi con có tồn tại không
        System.out.println("contains('Java'): " + text.contains("Java")); 
        
        // startsWith() / endsWith(): Kiểm tra bắt đầu / kết thúc bằng...
        System.out.println("startsWith('Pro'): " + text.startsWith("Pro"));
        System.out.println("endsWith('fun'): " + text.endsWith("fun"));
        
        // indexOf(): Tìm vị trí xuất hiện đầu tiên của chuỗi con/ký tự
        System.out.println("indexOf('a'): " + text.indexOf("a")); 
        
        // lastIndexOf(): Tìm vị trí xuất hiện cuối cùng
        System.out.println("lastIndexOf('a'): " + text.lastIndexOf("a")); 
    }

    private static void thaoTacVaBienDoi() {
        System.out.println("\n--- 5. Thao tác và Biến đổi (Tạo chuỗi mới) ---");
        String raw = "  Hello World  ";
        
        // toLowerCase() / toUpperCase(): In thường / In hoa toàn bộ
        System.out.println("toLowerCase: " + raw.toLowerCase());
        System.out.println("toUpperCase: " + raw.toUpperCase());
        
        // trim(): Xóa khoảng trắng 2 đầu (trước Java 11)
        System.out.println("trim: '" + raw.trim() + "'");
        
        // strip(): Xóa khoảng trắng 2 đầu, hỗ trợ chuẩn Unicode tốt hơn trim (Java 11+)
        System.out.println("strip: '" + raw.strip() + "'");
        System.out.println("stripLeading: '" + raw.stripLeading() + "'"); // Xóa đầu
        System.out.println("stripTrailing: '" + raw.stripTrailing() + "'"); // Xóa cuối
        
        // substring(start, end): Cắt chuỗi từ start đến (end - 1)
        String target = "JavaDeveloper";
        System.out.println("substring(4): " + target.substring(4)); 
        System.out.println("substring(0, 4): " + target.substring(0, 4)); 
        
        // replace(old, new): Thay thế ký tự / chuỗi con
        System.out.println("replace('o', '0'): " + "hello".replace('o', '0'));
        
        // replaceAll(regex, new): Thay thế theo biểu thức chính quy (Regex)
        System.out.println("replaceAll(số): " + "a1b2c3".replaceAll("\\d", "*")); 
        
        // replaceFirst(regex, new): Thay thế phần tử đầu tiên khớp với regex
        System.out.println("replaceFirst: " + "a1b2c3".replaceFirst("\\d", "*"));
    }

    private static void catVaNoiChuoi() {
        System.out.println("\n--- 6. Cắt (Split) và Nối (Join) chuỗi ---");
        
        // split(regex): Tách chuỗi thành mảng dựa trên ký tự phân cách
        String csv = "apple,banana,orange";
        String[] fruits = csv.split(",");
        System.out.println("split: " + Arrays.toString(fruits));
        
        // String.join(delimiter, elements): Nối các phần tử lại bằng chuỗi phân cách (Static method)
        String joined = String.join(" - ", fruits);
        System.out.println("String.join: " + joined);
        
        // concat(): Nối 2 chuỗi (Nên dùng dấu + thay vì concat cho tiện lợi)
        System.out.println("concat: " + "Hello ".concat("World"));
    }

    private static void chuyenDoiVaDinhDang() {
        System.out.println("\n--- 7. Chuyển đổi và Định dạng ---");
        
        // String.valueOf(): Chuyển mọi kiểu dữ liệu (int, double, boolean...) thành String
        System.out.println("String.valueOf(100): " + String.valueOf(100));
        
        // String.format(): Định dạng chuỗi (giống printf)
        String formatted = String.format("Tên tôi là %s, tôi %d tuổi", "Alex", 25);
        System.out.println("String.format: " + formatted);
    }

    private static void phuongThucNangCaoModernJava() {
        System.out.println("\n--- 8. Các phương thức Nâng cao (Java 11, 12, 15+) ---");
        
        // repeat(n): Lặp lại chuỗi n lần (Java 11+)
        System.out.println("repeat(3): " + "Ha".repeat(3));
        
        // lines(): Trả về một Stream các dòng, tách biệt bởi ký tự xuống dòng (Java 11+)
        String multiline = "Line 1\nLine 2\r\nLine 3";
        System.out.print("lines(): ");
        multiline.lines().forEach(line -> System.out.print("[" + line + "] "));
        System.out.println();

        // indent(n): Thêm n khoảng trắng vào đầu mỗi dòng (Java 12+)
        String text = "Java";
        System.out.println("indent(4):\n" + text.indent(4));
        
        // transform(): Áp dụng một hàm (Function) trực tiếp lên chuỗi (Java 12+)
        String transformed = "java".transform(s -> s.toUpperCase() + " 17");
        System.out.println("transform: " + transformed);
        
        // formatted(): Phương thức instance gọi trực tiếp định dạng (Thay vì String.format) (Java 15+)
        String f = "Giá: %.2f VNĐ".formatted(15000.50);
        System.out.println("formatted: " + f);
        
        // stripIndent(): Xóa các khoảng trắng thừa ở lề của một chuỗi nhiều dòng (Java 15+)
        String blockText = "   Thụt lề\n   Quá nhiều";
        System.out.println("stripIndent:\n" + blockText.stripIndent());
    }
}


boolean isPalindrome = true;
for (int i = 0; i < s.length() / 2; i++) {
    if (s.charAt(i) != s.charAt(s.length() - 1 - i)) {
        isPalindrome = false;
        break;
    }
}   

String s = "Java Programming";

// Duyệt xuôi
for (int i = 0; i < s.length(); i++) {
    System.out.print(s.charAt(i) + " ");
}

// Duyệt ngược (Đảo ngược chuỗi)
String reversed = "";
for (int i = s.length() - 1; i >= 0; i--) {
    reversed += s.charAt(i);
}

StringBuilder sb = new StringBuilder();
for (int i = 0; i < s.length(); i++) {
    sb.append(s.charAt(i));
}
String finalString = sb.toString();


/**
 * StringHandlingMaster.java
 * Tổng hợp toàn bộ các kỹ thuật xử lý chuỗi trong Java.
 */
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHandlingMaster {

    public static void main(String[] args) {
        // 1. Khởi tạo chuỗi
        String str = "  Chào mừng bạn đến với Java!  ";
        String s1 = "Java";
        String s2 = "java";

        System.out.println("--- 1. CƠ BẢN ---");
        System.out.println("Độ dài: " + str.length());
        System.out.println("Cắt khoảng trắng 2 đầu: '" + str.trim() + "'");
        System.out.println("Viết hoa: " + str.toUpperCase());
        System.out.println("Viết thường: " + str.toLowerCase());

        // 2. Kiểm tra nội dung
        System.out.println("\n--- 2. KIỂM TRA ---");
        System.out.println("Bắt đầu với '  Chào': " + str.startsWith("  Chào"));
        System.out.println("Kết thúc với '!': " + str.endsWith("!"));
        System.out.println("Chứa chữ 'Java': " + str.contains("Java"));
        System.out.println("So sánh chính xác: " + s1.equals(s2)); // false
        System.out.println("So sánh không phân biệt hoa thường: " + s1.equalsIgnoreCase(s2)); // true

        // 3. Trích xuất và tìm kiếm
        System.out.println("\n--- 3. TRÍCH XUẤT & TÌM KIẾM ---");
        System.out.println("Ký tự tại vị trí 5: " + str.charAt(5));
        System.out.println("Vị trí đầu tiên của 'a': " + str.indexOf("a"));
        System.out.println("Vị trí cuối cùng của 'a': " + str.lastIndexOf("a"));
        // Cắt chuỗi (substring): từ index 2 đến trước index 12
        System.out.println("Cắt chuỗi con: '" + str.substring(2, 12) + "'");

        // 4. Thay thế và Chia tách
        System.out.println("\n--- 4. THAY THẾ & CHIA TÁCH ---");
        String replaced = str.replace("Java", "World");
        System.out.println("Thay thế: " + replaced.trim());

        String list = "Apple,Banana,Orange";
        String[] fruits = list.split(",");
        System.out.println("Chia tách thành mảng: " + Arrays.toString(fruits));

        // 5. Nối chuỗi (Join)
        String joined = String.join(" - ", "2024", "2025", "2026");
        System.out.println("Nối chuỗi: " + joined);

        // 6. Hiệu năng với StringBuilder (Dùng khi cần thay đổi chuỗi nhiều lần)
        System.out.println("\n--- 5. STRINGBUILDER (HIỆU NĂNG) ---");
        StringBuilder sb = new StringBuilder("Hello");
        sb.append(" World");
        sb.insert(5, " Awesome");
        sb.reverse();
        System.out.println("Chuỗi đảo ngược: " + sb.toString());

        // 7. Biểu thức chính quy (Regex)
        System.out.println("\n--- 6. REGEX (KIỂM TRA ĐỊNH DẠNG) ---");
        String email = "contact@gemini.com";
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        System.out.println("Email '" + email + "' hợp lệ? " + matcher.matches());

        // 8. Định dạng chuỗi (Formatting)
        System.out.println("\n--- 7. ĐỊNH DẠNG (FORMAT) ---");
        String name = "Hoàng";
        int age = 25;
        String formatted = String.format("Tên: %s, Tuổi: %d", name, age);
        System.out.println(formatted);
    }
}