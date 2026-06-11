package utils;

import java.util.Scanner;

public class ValidationUtils {

    private static final Scanner scanner = new Scanner(System.in);

    public static String readNonEmptyString(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Error: Input cannot be empty!");
        }
    }

    public static String readPatternString(String prompt, String regex, String errorMessage) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.matches(regex)) {
                return input;
            }
            System.out.println(errorMessage);
        }
    }

    public static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value > 0) {
                    return value;
                }
                System.out.println("Error: Value must be positive (> 0)!");
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format!");
            }
        }
    }

    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Error: Value must be between " + min + " and " + max + "!");
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid integer format!");
            }
        }
    }

    public static String readPosition(String prompt) {
        String[] validPositions = {"Goalkeeper", "Defender", "Midfielder", "Forward", "Winger"};
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            for (String pos : validPositions) {
                if (pos.equalsIgnoreCase(input)) {
                    return pos;
                }
            }
            System.out.println("Error: Position must be one of: Goalkeeper, Defender, Midfielder, Forward, Winger!");
        }
    }

    public static String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readMenuChoice(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
