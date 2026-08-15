package tools;

import java.util.Scanner;
import model.Validatable;

public class Inputter {

    private static final Scanner sc = new Scanner(System.in);

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

    public static String inputLoop(String prompt, String regex, String errMsg) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (Validatable.isValid(value, regex)) return value;
            System.out.println(errMsg);
        }
    }

    public static String inputNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    public static String inputOptional(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

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
