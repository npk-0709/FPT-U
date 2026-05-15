import java.util.Scanner;

public class Inputter {

    private final Scanner ndl;

    public Inputter() {
        this.ndl = new Scanner(System.in);
    }

    public String getString(String mess) {
        System.out.print(mess);
        return ndl.nextLine().trim();
    }

    public int getInt(String mess) {
        while (true) {
            String input = getString(mess);
            if (Acceptable.isValid(input, Acceptable.INTEGER_VALID)) {
                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("[Error] Number is too large, please try again.");
                }
            } else {
                System.out.println("[Error] Please enter a valid integer.");
            }
        }
    }

    public double getDouble(String mess) {
        while (true) {
            String input = getString(mess);
            if (Acceptable.isValid(input, Acceptable.DOUBLE_VALID)) {
                try {
                    return Double.parseDouble(input);
                } catch (NumberFormatException e) {
                    System.out.println("[Error] Invalid number, please try again.");
                }
            } else {
                System.out.println("[Error] Please enter a valid number.");
            }
        }
    }

    public String inputAndLoop(String mess, String pattern) {
        while (true) {
            String input = getString(mess);
            if (Acceptable.isValid(input, pattern)) {
                return input;
            }
            System.out.println("[Error] Invalid format, please re-enter.");
        }
    }

    public String inputAndLoopAllowEmpty(String mess, String pattern) {
        while (true) {
            String input = getString(mess);
            if (input.isEmpty()) {
                return "";
            }
            if (Acceptable.isValid(input, pattern)) {
                return input;
            }
            System.out.println("[Error] Invalid format, please re-enter (or press Enter to keep old value).");
        }
    }

    public boolean confirmYesNo(String mess) {
        String answer = inputAndLoop(mess, Acceptable.YES_NO_VALID);
        return answer.equalsIgnoreCase("Y");
    }

    public int getMenuChoice(String mess, int min, int max) {
        while (true) {
            String input = getString(mess);
            if (Acceptable.isValid(input, Acceptable.INTEGER_VALID)) {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            }
            System.out.println("[Error] Please choose a number from " + min + " to " + max + ".");
        }
    }
}
