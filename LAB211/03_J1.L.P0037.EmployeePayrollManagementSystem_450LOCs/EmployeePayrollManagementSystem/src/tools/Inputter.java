package tools;

import java.util.List;
import java.util.Scanner;

/**
 * Tiện ích nhập liệu từ bàn phím có kiểm tra hợp lệ.
 *
 * <p>Mỗi phương thức nhập đều lặp lại cho tới khi người dùng nhập đúng,
 * tránh việc chương trình crash khi nhập sai kiểu/khoảng giá trị.</p>
 */
public final class Inputter {

    private static final Scanner SCANNER = new Scanner(System.in);

    private Inputter() {
    }

    /** Đọc một dòng nguyên bản (giữ nguyên kể cả chuỗi rỗng). */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine();
    }

    /** Nhập số nguyên trong khoảng [min, max]. */
    public static int inputInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("  -> Please enter a number in [" + min + ", " + max + "].");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("  -> Invalid number. Please try again.");
            }
        }
    }

    /** Nhập chuỗi không rỗng. */
    public static String inputNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("  -> Value cannot be empty. Please try again.");
        }
    }

    /** Nhập chuỗi khớp biểu thức chính quy (regex). */
    public static String inputByRegex(String prompt, String regex, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            if (line.matches(regex)) {
                return line;
            }
            System.out.println("  -> " + errorMessage);
        }
    }

    /** Nhập một giá trị thuộc tập cho phép (không phân biệt hoa thường). */
    public static String inputInSet(String prompt, List<String> allowed) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            for (String item : allowed) {
                if (item.equalsIgnoreCase(line)) {
                    return item; // chuẩn hoá về đúng dạng trong tập
                }
            }
            System.out.println("  -> Value must be one of " + allowed + ".");
        }
    }

    /** Nhập số thực dương ( > 0 ). */
    public static double inputPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value > 0) {
                    return value;
                }
                System.out.println("  -> Value must be a positive number.");
            } catch (NumberFormatException ex) {
                System.out.println("  -> Invalid number. Please try again.");
            }
        }
    }

    /** Nhập số thực không âm ( >= 0 ). */
    public static double inputNonNegativeDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value >= 0) {
                    return value;
                }
                System.out.println("  -> Value must be >= 0.");
            } catch (NumberFormatException ex) {
                System.out.println("  -> Invalid number. Please try again.");
            }
        }
    }

    /** Nhập tuỳ chọn cho chức năng Update: Enter để giữ giá trị cũ. */
    public static String inputOptional(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /** Hỏi xác nhận Yes/No, trả về true nếu người dùng chọn Y. */
    public static boolean confirmYesNo(String prompt) {
        String answer = inputByRegex(prompt, "^[YyNn]$", "Please enter Y or N.");
        return answer.equalsIgnoreCase("Y");
    }
}
