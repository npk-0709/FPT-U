
public class MyProcessor implements Processor {

    @Override
    public String reverseString(String input) {
        StringBuilder sb = new StringBuilder(input);
        return sb.reverse().toString();
    }

    @Override
    public int countVowels(String input) {
        int count = 0;
        String vowels = "AEIOUaeiou";
        for (char c : input.toCharArray()) {
            if (vowels.indexOf(c) != -1) {
                count++;
            }
        }
        return count;
    }


    @Override
    public int countSpecialCharacters(String input) {
        int count = 0;
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) {
                count++;
            }
        }
        return count;

    }
}
