package tools;

import java.util.List;
import java.util.Scanner;

public final class Inputter {

    private static final Scanner SCANNER = new Scanner(System.in);

    private Inputter() {
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine();
    }

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

    public static String inputInSet(String prompt, List<String> allowed) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            for (String item : allowed) {
                if (item.equalsIgnoreCase(line)) {
                    return item;
                }
            }
            System.out.println("  -> Value must be one of " + allowed + ".");
        }
    }

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

    public static String inputOptional(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    public static boolean confirmYesNo(String prompt) {
        String answer = inputByRegex(prompt, "^[YyNn]$", "Please enter Y or N.");
        return answer.equalsIgnoreCase("Y");
    }
}
