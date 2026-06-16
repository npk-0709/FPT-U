package tools;

import java.util.Scanner;
import model.Validatable;

/**
 * Lớp tiện ích nhập liệu từ bàn phím.
 * Cung cấp các phương thức static để nhập và validate dữ liệu.
 */
public class Inputter {

    private static final Scanner sc = new Scanner(System.in);

    /**
     * Nhập số nguyên trong khoảng [min, max].
     * Lặp lại cho đến khi nhập đúng.
     * @param prompt thông báo nhập
     * @param min    giá trị nhỏ nhất
     * @param max    giá trị lớn nhất
     * @return số nguyên hợp lệ
     */
    public static int inputInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(sc.nextLine().trim());
                if (value >= min && value <= max) return value;
                System.out.printf("Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    /**
     * Nhập chuỗi khớp regex pattern. Lặp lại nếu sai.
     * @param prompt thông báo nhập
     * @param regex  pattern cần khớp
     * @param errMsg thông báo khi sai format
     * @return chuỗi hợp lệ
     */
    public static String inputLoop(String prompt, String regex, String errMsg) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (Validatable.isValid(value, regex)) return value;
            System.out.println(errMsg);
        }
    }

    /**
     * Nhập chuỗi không rỗng. Lặp lại nếu rỗng.
     * @param prompt thông báo nhập
     * @return chuỗi không rỗng
     */
    public static String inputNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    /**
     * Nhập chuỗi tuỳ chọn (có thể rỗng — Enter để bỏ qua).
     * @param prompt thông báo nhập
     * @return chuỗi nhập vào (có thể rỗng)
     */
    public static String inputOptional(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    /**
     * Nhập số thực dương. Lặp lại nếu sai.
     * @param prompt thông báo nhập
     * @return số thực dương
     */
    public static double inputPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(sc.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Value must be a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    /**
     * Nhập xác nhận Yes/No. Lặp lại nếu nhập sai.
     * @param prompt thông báo nhập
     * @return true nếu người dùng nhập Y/y
     */
    public static boolean inputYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim().toUpperCase();
            if (value.equals("Y")) return true;
            if (value.equals("N")) return false;
            System.out.println("Please enter Y or N.");
        }
    }
}
