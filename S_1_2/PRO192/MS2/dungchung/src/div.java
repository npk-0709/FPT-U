public class MyUtilities implements IUtilities {

    @Override
    public int countSpecial(String str) {
        int count = 0;
        // The specific special characters we are looking for
        String specialChars = "!@#$%";
        
        for (int i = 0; i < str.length(); i++) {
            // Convert each char to a String to use the contains() method as hinted
            if (specialChars.contains(String.valueOf(str.charAt(i)))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int sumNumberDivisible3(String str) {
        int sum = 0;
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            
            // Check if the character is a digit
            if (Character.isDigit(c)) {
                // Get the numeric value of the character
                int digit = Character.getNumericValue(c);
                
                // Check if the digit is divisible by 3
                if (digit % 3 == 0) {
                    sum += digit;
                }
            }
        }
        return sum;
    }
}

